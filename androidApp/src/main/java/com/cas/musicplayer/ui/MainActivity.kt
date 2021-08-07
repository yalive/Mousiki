package com.cas.musicplayer.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.*
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.adcolony.sdk.AdColony
import com.cas.common.extensions.bool
import com.cas.common.extensions.fromDynamicLink
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.BuildConfig
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.ActivityMainBinding
import com.cas.musicplayer.di.Injector
import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.tmp.observe
import com.cas.musicplayer.tmp.observeEvent
import com.cas.musicplayer.ui.home.showExitDialog
import com.cas.musicplayer.ui.player.PlayerFragment
import com.cas.musicplayer.ui.settings.rate.askUserForFeelingAboutApp
import com.cas.musicplayer.utils.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.mopub.common.MoPub
import com.mopub.common.SdkConfiguration
import com.mousiki.shared.domain.models.LocalSong
import com.mousiki.shared.domain.models.YtbTrack
import com.mousiki.shared.domain.models.toYoutubeDuration
import com.mousiki.shared.preference.UserPrefs
import com.unity3d.ads.UnityAds
import kotlinx.coroutines.delay

class MainActivity : BaseActivity() {

    val adsViewModel by viewModel { Injector.adsViewModel }
    private val viewModel by viewModel { Injector.mainViewModel }
    private lateinit var navController: NavController

    private lateinit var playerFragment: PlayerFragment
    private var exitDialog: BottomSheetDialog? = null

    val binding by viewBinding(ActivityMainBinding::inflate)

    private var dialogDrawOverApps: AlertDialog? = null
    private var drawOverAppsRequested = false

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        initMediationSDK()
        UserPrefs.onLaunchApp()
        UserPrefs.resetNumberOfTrackClick()
        setContentView(binding.root)
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            updateBottomNavigationMenu(destination.id)
        }
        adsViewModel.apply {
            // just to prepare ads
        }
        setupPlayerFragment()
        binding.bottomNavView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navHome -> handleClickMenuHome()
                R.id.navLibrary -> handleClickMenuLibrary()
                R.id.navSearch -> handleClickMenuSearch()
                R.id.navMusic -> handleClickMenuMusic()
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

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            if (insets.top > 0) {
                DeviceInset.value =
                    ScreenInset(insets.left, insets.top, insets.right, insets.bottom)
            }
            binding.bottomNavView.updateLayoutParams<ConstraintLayout.LayoutParams> {
                bottomMargin = insets.bottom
            }
            var consumed = false
            val viewGroup = v as ViewGroup
            for (i in 0 until viewGroup.childCount) {
                val child = viewGroup.getChildAt(i)
                // Dispatch the insets to the child
                val childResult = ViewCompat.dispatchApplyWindowInsets(child, windowInsets)
                // If the child consumed the insets, record it
                if (childResult.isConsumed) {
                    consumed = true
                }
            }
            // If any of the children consumed the insets, return
            // an appropriate value
            if (consumed) WindowInsetsCompat.CONSUMED else windowInsets
        }

        if (!PreferenceUtil.musicSeen)
            binding.bottomNavView.getOrCreateBadge(R.id.navMusic)

        observe(PlayerQueue) { currentTrack ->
            if (!canDrawOverApps() && currentTrack !is LocalSong) {
                val dialog = Utils.requestDrawOverAppsPermission(this) {
                    drawOverAppsRequested = true
                }
                dialogDrawOverApps = dialog
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

    private fun updateBottomNavigationMenu(destinationId: Int) {
        when (destinationId) {
            R.id.homeFragment -> {
                binding.bottomNavView.menu[0].isChecked = true
            }
            R.id.localSongsContainerFragment -> {
                binding.bottomNavView.menu[1].isChecked = true
            }
            R.id.libraryFragment -> {
                binding.bottomNavView.menu[2].isChecked = true
            }
            R.id.mainSearchFragment -> {
                binding.bottomNavView.menu[3].isChecked = true
            }
        }
    }

    private fun handleClickMenuSearch() {
        if (navController.currentDestination?.id == R.id.mainSearchFragment) {
            viewModel.onDoubleClickSearchNavigation()
            return
        }
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

    private fun handleClickMenuMusic() {
        if (navController.currentDestination?.id == R.id.localSongsContainerFragment) return
        if (!navController.popBackStack(R.id.localSongsContainerFragment, false)) {
            navController.navigate(R.id.localSongsContainerFragment)
        }
        if (!PreferenceUtil.musicSeen){
            binding.bottomNavView.removeBadge(R.id.navMusic)
            PreferenceUtil.musicSeen = true
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        if (!wasLaunchedFromRecent() && intent.bool(EXTRAS_FROM_PLAYER_SERVICE)) {
            expandBottomPanel()
            if (intent.bool(EXTRAS_OPEN_BATTERY_SAVER_MODE)) {
                playerFragment.openBatterySaverMode()
            }
        }
        handleDynamicLinks()

        // Clean intent
        intent = intent.apply {
            putExtra(EXTRAS_FROM_PLAYER_SERVICE, false)
            putExtra(EXTRAS_OPEN_BATTERY_SAVER_MODE, false)
        }

        if (drawOverAppsRequested) {
            drawOverAppsRequested = false
            PlayerQueue.resume()
        }
    }

    override fun onDestroy() {
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
        playerFragment.collapsePlayer()
    }

    override fun onBackPressed() {
        if (binding.queueFragmentContainer.isVisible) {
            supportFragmentManager.findFragmentById(R.id.queueFragmentContainer)?.let {
                supportFragmentManager.beginTransaction().remove(it).commit()
            }
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
                        val track = YtbTrack(
                            youtubeId = videoId,
                            title = title,
                            duration = duration,
                            artistName = "",
                            artistId = ""
                        )
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
                val track = YtbTrack(
                    youtubeId = videoId,
                    title = title,
                    duration = YtbTrack.toYoutubeDuration(duration),
                    artistName = "",
                    artistId = ""
                )
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

        val testMode = BuildConfig.DEBUG
        UnityAds.initialize(this, getString(R.string.unity_rewarded_game_id), testMode)
    }

    private fun wasLaunchedFromRecent(): Boolean {
        val flags: Int = intent.flags and Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY
        return flags == Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY
    }

    companion object {
        const val EXTRAS_FROM_PLAYER_SERVICE = "from_player_service"
        const val EXTRAS_OPEN_BATTERY_SAVER_MODE = "start_battery_saver_mode"
    }
}