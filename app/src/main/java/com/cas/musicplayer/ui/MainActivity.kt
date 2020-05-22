package com.cas.musicplayer.ui

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.os.postDelayed
import androidx.core.view.ViewCompat
import androidx.core.view.get
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import androidx.transition.TransitionManager
import com.afollestad.materialdialogs.MaterialDialog
import com.cas.common.extensions.fromDynamicLink
import com.cas.common.extensions.isDarkMode
import com.cas.common.extensions.observe
import com.cas.common.extensions.observeEvent
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.MusicApp
import com.cas.musicplayer.R
import com.cas.musicplayer.di.injector.injector
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.domain.model.toYoutubeDuration
import com.cas.musicplayer.player.*
import com.cas.musicplayer.player.services.DragBottomPanelLiveData
import com.cas.musicplayer.player.services.DragPanelInfo
import com.cas.musicplayer.player.services.PlaybackLiveData
import com.cas.musicplayer.ui.home.showExitDialog
import com.cas.musicplayer.ui.home.view.InsetSlidingPanelView
import com.cas.musicplayer.ui.player.PlayerFragment
import com.cas.musicplayer.ui.settings.rate.askUserForFeelingAboutApp
import com.cas.musicplayer.utils.*
import com.crashlytics.android.Crashlytics
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : BaseActivity() {

    lateinit var slidingPaneLayout: InsetSlidingPanelView
    var isLocked = false
        set(value) {
            field = value
            onLockChanged(value)
        }

    val adsViewModel by viewModel { injector.adsViewModel }
    private val viewModel by viewModel { injector.mainViewModel }
    private lateinit var navController: NavController
    private var isFromService = false

    private var bottomView: ViewGroup? = null
    private var txtConnectivityState: TextView? = null
    private lateinit var playerFragment: PlayerFragment
    private var exitDialog: MaterialDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        UserPrefs.onLaunchApp()
        UserPrefs.resetNumberOfTrackClick()
        setContentView(R.layout.activity_main)
        bottomView = findViewById(R.id.bottomView)
        txtConnectivityState = findViewById(R.id.txtConnectivityState)
        slidingPaneLayout = findViewById(R.id.sliding_layout)
        setSupportActionBar(toolbar)
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            appbar.setExpanded(true, true)
            if (destination.id == R.id.homeFragment) {
                toolbar.title = getString(R.string.app_name)
            }
            updateBottomNavigationMenu(destination.id)
            val showBack = showBackForDestination(destination)
            supportActionBar?.setDisplayHomeAsUpEnabled(showBack)
            val currentEmplacement = VideoEmplacementLiveData.value
            if (currentEmplacement != null && currentEmplacement is EmplacementBottom) {
                VideoEmplacementLiveData.value = VideoEmplacement.bottom(bottomNavView.isVisible)
            }
        }

        if (!canDrawOverApps()) {
            requestDrawOverAppsPermission()
        }
        adsViewModel.apply {
            // just to prepare ads
        }
        setupPlayerFragment()
        ViewCompat.setOnApplyWindowInsetsListener(cordinator) { v, insets ->
            if (insets.systemWindowInsetTop > 0) {
                DeviceInset.value = ScreenInset(
                    insets.systemWindowInsetLeft,
                    insets.systemWindowInsetTop,
                    insets.systemWindowInsetRight,
                    insets.systemWindowInsetBottom
                )
            }
            var consumed = false
            val viewGroup = v as ViewGroup
            for (i in 0 until viewGroup.childCount) {
                val child = viewGroup.getChildAt(i)
                // Dispatch the insets to the child
                val childResult = ViewCompat.dispatchApplyWindowInsets(child, insets)
                // If the child consumed the insets, record it
                if (childResult.isConsumed) {
                    consumed = true
                }
            }
            // If any of the children consumed the insets, return
            // an appropriate value
            if (consumed) insets.consumeSystemWindowInsets() else insets
        }

        bottomNavView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navHome -> handleClickMenuHome()
                R.id.navLibrary -> handleClickMenuLibrary()
                R.id.navSearch -> handleClickMenuSearch()
                else -> {
                }
            }
            true
        }

        observe(viewModel.connectivityState) { state ->
            handler.postDelayed(if (state.isConnected) 500L else 0) {
                bottomView?.let { viewGroup ->
                    TransitionManager.beginDelayedTransition(viewGroup)
                }
                txtConnectivityState?.isGone = state.isConnected
            }
            if (state.isConnected) {
                txtConnectivityState?.setBackgroundColor(color(R.color.colorGreenState))
                txtConnectivityState?.setText(R.string.connection_back)
            } else {
                txtConnectivityState?.setBackgroundColor(color(R.color.colorDarkNavigationView))
                txtConnectivityState?.setText(R.string.no_connection)
            }
            VideoEmplacementLiveData.forceUpdate()
        }

        if (savedInstanceState == null) {
            viewModel.checkToRateApp()
        }

        if (isFromPushNotification()) {
            checkPushNotificationTrack()
        } else if (!intent.fromDynamicLink() && !comeFromPlayerService()) {
            observeEvent(viewModel.rateApp) {
                askUserForFeelingAboutApp()
            }
        }
        viewModel.checkStartFromShortcut(intent.data?.toString())

        observe(VideoEmplacementLiveData) { emplacement ->
            val playBackState = PlaybackLiveData.value
            if (emplacement is EmplacementBottom && playBackState != null && playBackState != PlayerConstants.PlayerState.UNKNOWN) {
                collapseBottomPanel()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showBackForDestination(destination: NavDestination): Boolean {
        return destination.id == R.id.favouriteSongsFragment
                || destination.id == R.id.settingsFragment
                || destination.id == R.id.genresFragment
                || destination.id == R.id.addTrackToPlaylistFragment
                || destination.id == R.id.createPlaylistFragment
                || destination.id == R.id.artistsFragment
    }

    private fun updateBottomNavigationMenu(destinationId: Int) {
        when (destinationId) {
            R.id.homeFragment -> {
                bottomNavView.menu[0].isChecked = true
            }
            R.id.libraryFragment -> {
                bottomNavView.menu[1].isChecked = true
            }
        }
        val showBottomBarForDestination = showBottomBarForDestination(destinationId)
        bottomNavView.isVisible = showBottomBarForDestination
    }

    private fun showBottomBarForDestination(destinationId: Int): Boolean {
        return (destinationId == R.id.homeFragment
                || destinationId == R.id.libraryFragment
                || destinationId == R.id.mainSearchFragment)
    }

    private fun handleClickMenuSearch() {
        if (navController.currentDestination?.id == R.id.mainSearchFragment) {
            viewModel.onDoubleClickSearchNavigation()
            return
        }
        appbar.setExpanded(true, true)
        navController.navigate(R.id.mainSearchFragment)
    }

    private fun handleClickMenuHome() {
        navController.popBackStack(R.id.homeFragment, false)
    }

    private fun handleClickMenuLibrary() {
        if (navController.currentDestination?.id == R.id.libraryFragment) return
        navController.navigate(R.id.libraryFragment)
    }

    private var openBatterySaver = false
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        isFromService = intent?.getBooleanExtra(EXTRAS_FROM_PLAY_SERVICE, false) ?: false
        openBatterySaver = intent?.getBooleanExtra(EXTRAS_OPEN_BATTERY_SAVER_MODE, false) ?: false
    }

    override fun onResume() {
        super.onResume()
        if (!isFromService) {
            isFromService = intent.getBooleanExtra(EXTRAS_FROM_PLAY_SERVICE, false)
        }

        val emplacement = VideoEmplacementLiveData.oldValue1

        if (emplacement is EmplacementFullScreen || isLandscape()) {
            showStatusBar()
            switchToPortrait()
            VideoEmplacementLiveData.center()
            // To be sure
            handler.postDelayed({
                VideoEmplacementLiveData.center()
            }, 1000)

        } else if (isFromService) {
            isFromService = false
            if (emplacement is EmplacementPlaylist) {
                VideoEmplacementLiveData.playlist()
            } else {
                expandBottomPanel()
                VideoEmplacementLiveData.center()
                bottomPanelFragment()?.onPanelSlide(slidingPaneLayout, 1f)
            }

            if (openBatterySaver) {
                expandBottomPanel()
                VideoEmplacementLiveData.center()
                playerFragment.openBatterySaverMode()
                openBatterySaver = false
            }
        } else {
            // Restore old video state if any
            restoreOldVideoState()
        }
        ViewCompat.requestApplyInsets(cordinator)
        if (canDrawOverApps()) {
            handleDynamicLinks()
        }

        lifecycleScope.launch {
            delay(300)
            if (queueFragmentContainer.isVisible) {
                PlayerQueue.hideVideo()
            }
        }
    }


    override fun onPause() {
        super.onPause()
        // Save current state
        viewModel.lastVideoEmplacement = VideoEmplacementLiveData.value
        // Movable video
        VideoEmplacementLiveData.out()
    }

    override fun onDestroy() {
        exitDialog?.dismiss()
        dialogDrawOverApps?.dismiss()
        super.onDestroy()
    }

    private fun bottomPanelFragment(): PlayerFragment? {
        return supportFragmentManager.findFragmentById(R.id.playerFragment) as? PlayerFragment
    }

    private fun setupPlayerFragment() {
        playerFragment =
            supportFragmentManager.findFragmentById(R.id.playerFragment) as? PlayerFragment
                ?: PlayerFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.playerFragment, playerFragment)
            .commit()

        hideBottomPanel()
        slidingPaneLayout.addPanelSlideListener(object :
            SlidingUpPanelLayout.SimplePanelSlideListener() {
            override fun onPanelSlide(panel: View, slideOffset: Float) {
                DragBottomPanelLiveData.value = DragPanelInfo(panel.y, slideOffset)
            }

            override fun onPanelStateChanged(
                panel: View,
                previousState: SlidingUpPanelLayout.PanelState,
                newState: SlidingUpPanelLayout.PanelState?
            ) {
                if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    window.statusBarColor = Color.TRANSPARENT
                    darkStatusBar()
                    bottomNavView.isVisible = false
                    VideoEmplacementLiveData.center()
                } else if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    adjustStatusBarWhenPanelCollapsed()
                    bottomNavView.isVisible =
                        showBottomBarForDestination(navController.currentDestination!!.id)
                    VideoEmplacementLiveData.bottom(bottomNavView.isVisible)
                } else if (newState != SlidingUpPanelLayout.PanelState.DRAGGING) {
                    bottomNavView.isVisible =
                        showBottomBarForDestination(navController.currentDestination!!.id)
                }
            }
        })

        PlaybackLiveData.observe(this, Observer {
            if (it == PlayerConstants.PlayerState.UNKNOWN) {
                hideBottomPanel()
            }
        })

    }

    private fun adjustStatusBarWhenPanelCollapsed() {
        val id = navController.currentDestination?.id
        if (id == R.id.settingsFragment
            || id == R.id.libraryFragment
            || id == R.id.mainSearchFragment
            || id == R.id.createPlaylistFragment
            || id == R.id.genresFragment
            || id == R.id.favouriteSongsFragment
            || id == R.id.artistsFragment
        ) {
            if (isDarkMode() || id == R.id.createPlaylistFragment) {
                window.statusBarColor = Color.BLACK
                darkStatusBar()
            } else {
                window.statusBarColor = Color.WHITE
                lightStatusBar()
            }
        } else {
            darkStatusBar()
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    override fun onBackPressed() {
        if (queueFragmentContainer.isVisible) {
            supportFragmentManager.findFragmentById(R.id.queueFragmentContainer)?.let {
                supportFragmentManager.beginTransaction().remove(it).commit()
            }
            PlayerQueue.showVideo()
            queueFragmentContainer.isVisible = false
            playerFragment.onQueueClosed()
            return
        }
        if (VideoEmplacementLiveData.value is EmplacementFullScreen) {
            showStatusBar()
            switchToPortrait()
            VideoEmplacementLiveData.center()
            return
        }

        if (isBottomPanelExpanded() && !isLocked) {
            collapseBottomPanel()
            return
        } else if (isLocked) {
            return
        }

        if (navController.isHome()) {
            exitDialog = showExitDialog()
        } else {
            super.onBackPressed()
        }
    }

    /**
     * Restore state: Not from service
     */
    private fun restoreOldVideoState() {
        val playBackState = PlaybackLiveData.value
        val lastVideoEmplacement = viewModel.lastVideoEmplacement
        if (lastVideoEmplacement != null && playBackState != null && playBackState != PlayerConstants.PlayerState.UNKNOWN) {
            // Usually for coming from background
            if (lastVideoEmplacement !is EmplacementOut) {
                VideoEmplacementLiveData.value = lastVideoEmplacement
            }
        } else if (playBackState != null && playBackState != PlayerConstants.PlayerState.UNKNOWN && VideoEmplacementLiveData.value is EmplacementOut) {
            collapseBottomPanel()
            VideoEmplacementLiveData.bottom(true)
        } else {
            hideBottomPanel()
        }
    }


    fun hideBottomPanel() {
        slidingPaneLayout.panelState = SlidingUpPanelLayout.PanelState.HIDDEN
    }

    fun expandBottomPanel() {
        slidingPaneLayout.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
    }

    fun collapseBottomPanel() {
        if (!canDrawOverApps()) {
            requestDrawOverAppsPermission()
            return
        }
        slidingPaneLayout.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
    }

    fun isBottomPanelCollapsed(): Boolean {
        return slidingPaneLayout.panelState == SlidingUpPanelLayout.PanelState.COLLAPSED
    }

    fun isBottomPanelExpanded(): Boolean {
        return slidingPaneLayout.panelState == SlidingUpPanelLayout.PanelState.EXPANDED
    }

    var dialogDrawOverApps: AlertDialog? = null

    private fun requestDrawOverAppsPermission() {
        dialogDrawOverApps = AlertDialog.Builder(this).setCancelable(false)
            .setMessage(R.string.message_enable_draw)
            .setNegativeButton(getString(R.string.btn_deny)) { _, _ ->
            }.setPositiveButton(getString(R.string.btn_agree)) { _, _ ->
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                if (intent.resolveActivity(packageManager) != null) {
                    startActivityForResult(intent, 10)
                } else {
                    MusicApp.get().toast(R.string.message_enable_draw_over_apps_manually)
                    Crashlytics.log("requestDrawOverAppsPermission intent not resolved")
                }
            }.show()
    }

    private fun onLockChanged(locked: Boolean) {
        slidingPaneLayout.isTouchEnabled = !locked
    }

    private fun handleDynamicLinks() {
        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                    val videoId = deepLink?.getQueryParameter("videoId")
                    val duration = deepLink?.getQueryParameter("duration")
                    val title = deepLink?.getQueryParameter("title")
                    if (videoId != null && title != null && duration != null) {
                        val track = MusicTrack(videoId, title, duration)
                        expandBottomPanel()
                        viewModel.playTrackFromDeepLink(track)
                    }
                }
            }
    }

    private fun checkPushNotificationTrack() {
        if (!canDrawOverApps()) return
        lifecycleScope.launchWhenResumed {
            delay(100)
            val videoId = intent.extras?.getString("videoId")
            val duration = intent.extras?.getString("duration")
            val title = intent.extras?.getString("title")
            if (videoId != null && title != null && duration != null) {
                val track = MusicTrack(videoId, title, MusicTrack.toYoutubeDuration(duration))
                expandBottomPanel()
                viewModel.playTrackFromPushNotification(track)
            }
        }
    }

    private fun isFromPushNotification(): Boolean {
        val videoId = intent.extras?.getString("videoId")
        val duration = intent.extras?.getString("duration")
        val title = intent.extras?.getString("title")
        return videoId != null && title != null && duration != null
    }

    private fun comeFromPlayerService() = intent.hasExtra(EXTRAS_FROM_PLAY_SERVICE)

    companion object {
        const val EXTRAS_FROM_PLAY_SERVICE = "from_player_service"
        const val EXTRAS_OPEN_BATTERY_SAVER_MODE = "start_battery_saver_mode"
    }
}
