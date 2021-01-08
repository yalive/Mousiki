package com.cas.musicplayer.ui

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import com.adcolony.sdk.AdColony
import com.cas.common.extensions.bool
import com.cas.common.extensions.fromDynamicLink
import com.cas.common.extensions.isDarkMode
import com.cas.common.extensions.observeEvent
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.ActivityMainBinding
import com.cas.musicplayer.di.injector.injector
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.domain.model.toYoutubeDuration
import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.ui.home.showExitDialog
import com.cas.musicplayer.ui.player.PlayerFragment
import com.cas.musicplayer.ui.player.TAG_SERVICE
import com.cas.musicplayer.ui.settings.rate.askUserForFeelingAboutApp
import com.cas.musicplayer.utils.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.mopub.common.MoPub
import com.mopub.common.SdkConfiguration
import kotlinx.coroutines.delay

private const val TAG_NAV = "MainActivity_nav"

class MainActivity : BaseActivity() {

    var isLocked = false

    val adsViewModel by viewModel { injector.adsViewModel }
    private val viewModel by viewModel { injector.mainViewModel }
    private lateinit var navController: NavController

    private lateinit var playerFragment: PlayerFragment
    private var exitDialog: BottomSheetDialog? = null

    val binding by viewBinding(ActivityMainBinding::inflate)

    private var dialogDrawOverApps: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        initMediationSDK()
        UserPrefs.onLaunchApp()
        UserPrefs.resetNumberOfTrackClick()
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            binding.appbar.setExpanded(true, true)
            if (destination.id == R.id.homeFragment) {
                binding.toolbar.title = getString(R.string.app_name)
            }
            updateBottomNavigationMenu(destination.id)
            val showBack = showBackForDestination(destination)
            supportActionBar?.setDisplayHomeAsUpEnabled(showBack)
        }

        if (!canDrawOverApps()) {
            Utils.requestDrawOverAppsPermission(this).also {
                dialogDrawOverApps = it
            }
        }
        adsViewModel.apply {
            // just to prepare ads
        }
        setupPlayerFragment()

        ViewCompat.setOnApplyWindowInsetsListener(binding.coordinator) { v, insets ->
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

        binding.bottomNavView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navHome -> handleClickMenuHome()
                R.id.navLibrary -> handleClickMenuLibrary()
                R.id.navSearch -> handleClickMenuSearch()
                else -> {
                }
            }
            true
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
                binding.bottomNavView.menu[0].isChecked = true
            }
            R.id.libraryFragment -> {
                binding.bottomNavView.menu[1].isChecked = true
            }
            R.id.mainSearchFragment -> {
                binding.bottomNavView.menu[2].isChecked = true
            }
        }
    }

    private fun handleClickMenuSearch() {
        if (navController.currentDestination?.id == R.id.mainSearchFragment) {
            viewModel.onDoubleClickSearchNavigation()
            return
        }
        binding.appbar.setExpanded(true, true)
        if (!navController.popBackStack(R.id.mainSearchFragment, false)) {
            navController.navigate(R.id.mainSearchFragment)
        }
    }

    private fun handleClickMenuHome() {
        navController.popBackStack(R.id.homeFragment, false)
    }

    private fun handleClickMenuLibrary() {
        if (navController.currentDestination?.id == R.id.libraryFragment) return
        if (!navController.popBackStack(R.id.libraryFragment, false)) {
            navController.navigate(R.id.libraryFragment)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        //Log.d(TAG_SERVICE, "onNewIntent: ${intent?.dumpData()}")
    }

    override fun onResume() {
        super.onResume()
        //Log.d(TAG_SERVICE, "onResume: from service:${intent.bool(EXTRAS_FROM_PLAYER_SERVICE)}")
        if (!wasLaunchedFromRecent() && intent.bool(EXTRAS_FROM_PLAYER_SERVICE)) {
            expandBottomPanel()
            if (intent.bool(EXTRAS_OPEN_BATTERY_SAVER_MODE)) {
                Log.d(TAG_SERVICE, "onResume: OPEN SAVE MODE FROM ACTIVITY")
                playerFragment.openBatterySaverMode()
            }
        }
        ViewCompat.requestApplyInsets(binding.coordinator)
        handleDynamicLinks()


        // Clean intent
        intent = intent.apply {
            putExtra(EXTRAS_FROM_PLAYER_SERVICE, false)
            putExtra(EXTRAS_OPEN_BATTERY_SAVER_MODE, false)
        }
    }

    override fun onPause() {
        super.onPause()
        //Log.d(TAG_SERVICE, "onPause ")
        // Movable video
        // VideoEmplacementLiveData.out()
    }

    override fun onDestroy() {
        Log.d(TAG_SERVICE, "onDestroy: activity")
        exitDialog?.dismiss()
        dialogDrawOverApps?.dismiss()
        super.onDestroy()
    }

    private fun setupPlayerFragment() {
        playerFragment = supportFragmentManager.findFragmentById(R.id.playerContainer)
                as? PlayerFragment ?: PlayerFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.playerContainer, playerFragment)
            .commit()
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
        if (binding.queueFragmentContainer.isVisible) {
            supportFragmentManager.findFragmentById(R.id.queueFragmentContainer)?.let {
                supportFragmentManager.beginTransaction().remove(it).commit()
            }
            PlayerQueue.showVideo()
            binding.queueFragmentContainer.isVisible = false
            playerFragment.onQueueClosed()
            return
        }

        if (playerFragment.handleBackPress()) return

        if (navController.isHome()) {
            exitDialog = showExitDialog()
        } else {
            super.onBackPressed()
        }
    }

    fun expandBottomPanel() {
        playerFragment.expandPlayer()
    }

    fun collapseBottomPanel() {
        if (!canDrawOverApps()) {
            Utils.requestDrawOverAppsPermission(this).also {
                dialogDrawOverApps = it
            }
            return
        }
        playerFragment.collapsePlayer()
    }

    fun isBottomPanelExpanded(): Boolean {
        if (playerFragment.view == null) return false
        return playerFragment.isExpanded()
    }

    private fun handleDynamicLinks() {
        if (!canDrawOverApps()) return
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

    private fun comeFromPlayerService() = intent.hasExtra(EXTRAS_FROM_PLAYER_SERVICE)

    private fun initMediationSDK() {
        val sdkConfiguration = SdkConfiguration.Builder("bc645649938646db9030829e2d969ad8").build()
        MoPub.initializeSdk(this, sdkConfiguration, null)
        AdColony.configure(
            this,
            "appee158214620447b7ba",
            "vzc26139c68efb46f492", "vz59b9a39b315e495b9c"
        )
    }

    fun wasLaunchedFromRecent(): Boolean {
        val flags: Int = intent.flags and Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY
        return flags == Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY
    }

    companion object {
        const val EXTRAS_FROM_PLAYER_SERVICE = "from_player_service"
        const val EXTRAS_OPEN_BATTERY_SAVER_MODE = "start_battery_saver_mode"
    }
}
