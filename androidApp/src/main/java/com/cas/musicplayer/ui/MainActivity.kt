package com.cas.musicplayer.ui

import android.annotation.SuppressLint
import android.app.PictureInPictureParams
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.*
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.adcolony.sdk.AdColony
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.getActionButton
import com.afollestad.materialdialogs.checkbox.checkBoxPrompt
import com.cas.common.extensions.bool
import com.cas.common.extensions.fromDynamicLink
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.BuildConfig
import com.cas.musicplayer.MusicApp
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.ActivityMainBinding
import com.cas.musicplayer.di.Injector
import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.player.services.PlaybackLiveData
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
        binding.bottomNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navHome -> handleClickMenuHome()
                R.id.navVideo -> handleClickMenuVideo()
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

        if (!PreferenceUtil.videoSeen)
            binding.bottomNavView.getOrCreateBadge(R.id.navVideo)

        observe(PlayerQueue) { currentTrack ->
            if (!SystemSettings.canEnterPiPMode() && !canDrawOverApps() && currentTrack !is LocalSong) {
                if (SystemSettings.isPiPSupported()) {
                    if (PreferenceUtil.showPipDialog) {
                        var dontAskMeAgain = false
                        MaterialDialog(this).show {
                            message(R.string.pip_dialog_mesage)
                            if (PreferenceUtil.askPipPermissionCount >= 2) {
                                checkBoxPrompt(R.string.pip_dialog_checkbox_dont_ask_me_again) {
                                    dontAskMeAgain = it
                                }
                            }
                            title(R.string.pip_dialog_title)
                            positiveButton(R.string.yes) {
                                SystemSettings.openPipSetting(this@MainActivity)
                            }
                            negativeButton(R.string.no) {
                                PreferenceUtil.showPipDialog = !dontAskMeAgain
                            }
                            getActionButton(WhichButton.NEGATIVE).updateTextColor(Color.parseColor("#808184"))
                        }
                        PreferenceUtil.askPipPermissionCount++
                    }
                } else {
                    val dialog = Utils.requestDrawOverAppsPermission(this) {
                        drawOverAppsRequested = true
                    }
                    dialogDrawOverApps = dialog
                }
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (playerFragment.handleBackPress()) return
                if (navController.isHome()) {
                    exitDialog = showExitDialog()
                } else {
                    navController.popBackStack()
                }
            }
        })

        lifecycleScope.launchWhenStarted {
            binding.bottomNavView.selectedItemId = R.id.navMusic
        }
    }

    @SuppressLint("NewApi")
    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (SystemSettings.canEnterPiPMode() && PlaybackLiveData.isPlaying() && PlayerQueue.value is YtbTrack) {
            enterPictureInPictureMode(PictureInPictureParams.Builder().build())
        }
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration?
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        binding.pipVideoView.isVisible = isInPictureInPictureMode
        if (isInPictureInPictureMode) {
            showPipPlayerView()
        } else if (!MusicApp.get().isInForeground) {
            // Make sure to pause player if playing Ytb track
            if (PlaybackLiveData.isPlaying() && PlayerQueue.value is YtbTrack) {
                PlayerQueue.pause()
            }
        }
    }

    fun showPipPlayerView() {
        val playerView = playerFragment.reusedPlayerView ?: return
        if (playerView.parent == binding.pipVideoView) return
        val oldParent = playerView.parent as? ViewGroup
        oldParent?.removeView(playerView)
        binding.pipVideoView.addView(playerView, 0)
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
            R.id.localVideoContainerFragment -> {
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

    private fun handleClickMenuVideo() {
        if (navController.currentDestination?.id == R.id.localVideoContainerFragment) return
        if (!navController.popBackStack(R.id.localVideoContainerFragment, false)) {
            navController.navigate(R.id.localVideoContainerFragment)
        }
        if (!PreferenceUtil.videoSeen) {
            binding.bottomNavView.removeBadge(R.id.navVideo)
            PreferenceUtil.videoSeen = true
        }
    }

    private fun handleClickMenuMusic() {
        if (navController.currentDestination?.id == R.id.localSongsContainerFragment) return
        if (!navController.popBackStack(R.id.localSongsContainerFragment, false)) {
            navController.navigate(R.id.localSongsContainerFragment)
        }
        if (!PreferenceUtil.musicSeen) {
            binding.bottomNavView.removeBadge(R.id.navMusic)
            PreferenceUtil.musicSeen = true
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    @SuppressLint("NewApi")
    override fun onResume() {
        super.onResume()
        if (!wasLaunchedFromRecent() && intent.bool(EXTRAS_FROM_PLAYER_SERVICE)) {
            expandBottomPanel()
            if (intent.bool(EXTRAS_OPEN_BATTERY_SAVER_MODE)) {
                playerFragment.openBatterySaverMode()
            }
        }
        handleDynamicLinks()

        // Check open audio track with Mousiki/ or via share
        // exclude search and new_releases shortcuts
        val uri = intent?.data ?: intent?.getParcelableExtra(Intent.EXTRA_STREAM)
        if (uri != null && !uri.toString().startsWith("mousiki", true)) {
            if (SongsUtil.playFromUri(this, uri)) {
                expandBottomPanel()
            } else {
                //error can't play this song
                Toast.makeText(
                    this,
                    getString(R.string.error_cannot_play_local_song),
                    Toast.LENGTH_LONG
                ).show()
            }

            intent = Intent()
        }

        // Clean intent
        intent = intent.apply {
            putExtra(EXTRAS_FROM_PLAYER_SERVICE, false)
            putExtra(EXTRAS_OPEN_BATTERY_SAVER_MODE, false)
        }

        if (drawOverAppsRequested) {
            drawOverAppsRequested = false
            PlayerQueue.resume()
        }

        // Check PIP
        if (SystemSettings.canEnterPiPMode() && intent.getBooleanExtra(EXTRA_START_PIP, false)) {
            enterPictureInPictureMode(PictureInPictureParams.Builder().build())
            intent = intent.apply {
                putExtra(EXTRA_START_PIP, false)
            }
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
        const val EXTRA_START_PIP = "start_pip"
    }
}