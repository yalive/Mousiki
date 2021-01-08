package com.cas.musicplayer.ui.player

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.os.Bundle
import android.os.IBinder
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.TransitionAdapter
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.getActionButton
import com.cas.common.extensions.observe
import com.cas.common.extensions.onClick
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.FragmentPlayerBinding
import com.cas.musicplayer.di.injector.injector
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.player.extensions.toText
import com.cas.musicplayer.player.services.FavouriteReceiver
import com.cas.musicplayer.player.services.MusicPlayerService
import com.cas.musicplayer.player.services.PlaybackDuration
import com.cas.musicplayer.player.services.PlaybackLiveData
import com.cas.musicplayer.ui.MainActivity
import com.cas.musicplayer.ui.player.queue.QueueFragment
import com.cas.musicplayer.ui.player.view.animateProgress
import com.cas.musicplayer.utils.*
import com.google.android.gms.ads.AdRequest
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import it.sephiroth.android.library.xtooltip.ClosePolicy
import it.sephiroth.android.library.xtooltip.Tooltip
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

/**
 ************************************
 * Created by Abdelhadi on 11/28/20.
 * Copyright © 2020 Mousiki
 ************************************
 */
const val TAG_SERVICE = "service_playback"

class PlayerFragment : Fragment(R.layout.fragment_player) {
    private val TAG = "player_view"
    private val binding by viewBinding(FragmentPlayerBinding::bind)
    private val viewModel by viewModel { injector.playerViewModel }
    private var seekingDuration = false

    private var mediaController: MediaControllerCompat? = null
    private var playerService: MusicPlayerService? = null

    private val reusedPlayerView: YouTubePlayerView?
        get() = playerService?.getPlayerView()

    private var serviceBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d(TAG_SERVICE, "onServiceDisconnected")
            playerService = null
            serviceBound = false
        }

        override fun onServiceConnected(name: ComponentName?, binder: IBinder) {
            Log.d(TAG_SERVICE, "onServiceConnected")
            val service = (binder as MusicPlayerService.ServiceBinder).service()
            playerService = service
            mediaController = MediaControllerCompat(requireContext(), service.mediaSession)
            mediaController?.registerCallback(mediaControllerCallback)
            mediaController?.playbackState?.let { onPlayMusicStateChanged(it) }
            showPlayerView("service connected")
            if (PlayerQueue.value != null) {
                ensurePlayerVisible()
            }
        }

        override fun onBindingDied(name: ComponentName?) {
            super.onBindingDied(name)
            Log.d(TAG_SERVICE, "onBindingDied: $name")
            unbindService()
            bindServiceIfNecessary()
        }

        override fun onNullBinding(name: ComponentName?) {
            super.onNullBinding(name)
            Log.d(TAG_SERVICE, "onNullBinding: $name")
            unbindService()
            bindServiceIfNecessary()
        }
    }

    private val mediaControllerCallback = object : MediaControllerCompat.Callback() {
        override fun binderDied() {
            Log.d(TAG_SERVICE, "binderDied: ")
            super.binderDied()
            hidePlayer()
        }

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            Log.d(TAG, "onPlaybackStateChanged: ${state?.toText()}")
            state?.let { onPlayMusicStateChanged(state) }
        }

        override fun onSessionDestroyed() {
            Log.d(TAG_SERVICE, "onSessionDestroyed: ")
            serviceBound = false
            hidePlayer()
            bindServiceIfNecessary()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG_SERVICE, "onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        setupView()
        observeViewModel()
        bindServiceIfNecessary()
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG_SERVICE, "onStart fragment")
        VideoEmplacementLiveData.inApp()
        viewModel.prepareAds()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG_SERVICE, "onResume fragment")

        mediaController?.playbackState?.let { onPlayMusicStateChanged(it) }
        if (binding.lockScreenView.isVisible) {
            checkLockScreen(true)
        }

        // Util when service is killed and fragment is resumed after that.
        // Need to check if service is running
        if (!requireContext().isServiceRunning(MusicPlayerService::class.java)) {
            hidePlayer()
        }

        // check if no track currently playing
        if (PlayerQueue.value == null) {
            hidePlayer()
        }

        // Make sure video is visible if service is bound
        showPlayerView("on resume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG_SERVICE, "onPause fragment")
        if (binding.lockScreenView.isVisible) {
            binding.lockScreenView.disableLock()
            binding.motionLayout.getTransition(R.id.mainTransition).setEnable(true)
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG_SERVICE, "onStop fragment")
        // Movable video
        VideoEmplacementLiveData.out()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG_SERVICE, "onDestroyView fragment")
        unbindService()
    }

    private fun unbindService() {
        Log.d(TAG, "call to unbindService: serviceBound=$serviceBound")
        if (serviceBound) {
            serviceBound = false
            mediaController?.unregisterCallback(mediaControllerCallback)
            activity?.unbindService(serviceConnection)
            playerService = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG_SERVICE, "onDestroy fragment")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RQ_CODE_WRITE_SETTINGS) {
            val canWriteSettings = SystemSettings.canWriteSettings(requireContext())
                    && SystemSettings.canDrawOverApps(requireContext())
            if (canWriteSettings) {
                lifecycleScope.launchWhenResumed {
                    delay(500)
                    expandPlayer()
                }
                openBatterySaverMode()
            }
        }
    }

    fun handleBackPress(): Boolean {
        if (binding.lockScreenView.isVisible) return true
        if (isExpanded()) {
            collapsePlayer()
            return true
        }
        return false
    }

    private fun bindServiceIfNecessary() {
        if (!serviceBound && PlayerQueue.value != null) {
            val intent = Intent(requireContext().applicationContext, MusicPlayerService::class.java)
            val resultBind =
                activity?.bindService(
                    intent,
                    serviceConnection,
                    Context.BIND_IMPORTANT and Context.BIND_AUTO_CREATE
                )
            if (resultBind == true) {
                serviceBound = true
            }
            Log.d(TAG_SERVICE, "bindServiceIfNecessary: resultBind=$resultBind")
        } else {
            Log.d(
                TAG_SERVICE,
                "bindServiceIfNecessary: already bound (serviceBound=$serviceBound,PlayerQueue.value=${PlayerQueue.value})"
            )
        }
    }

    private fun setupView() {
        setupMotionLayout()
        binding.btnPlayOption.setImageResource(UserPrefs.getSort().icon)
        setUpUserEvents()
        observe(DeviceInset) { inset ->
            binding.fullScreenSwitchView.updatePadding(top = inset.top)
        }

        if (viewModel.bannerAdOn()) {
            binding.bannerAdView.loadAd(AdRequest.Builder().build())
        } else {
            binding.bannerAdView.isVisible = false
        }
        binding.txtTitle.isSelected = true
    }

    private fun setUpUserEvents() {
        binding.btnPlayPauseMain.onClick {
            onClickPlayPause()
        }

        binding.btnShareVia.onClick {
            Utils.shareWithDeepLink(PlayerQueue.value, requireContext())
        }

        binding.btnAddFav.onClick {
            val isFav = UserPrefs.isFav(PlayerQueue.value?.youtubeId)
            if (!isFav) {
                Executors.newSingleThreadExecutor().execute {
                    val musicTrack = PlayerQueue.value
                    musicTrack?.let {
                        viewModel.makeSongAsFavourite(it)
                    }
                }
                binding.btnAddFav.setImageResource(R.drawable.ic_heart_solid)
                binding.btnAddFav.tint(R.color.colorAccent)
            } else {
                val musicTrack = PlayerQueue.value
                musicTrack?.let {
                    viewModel.removeSongFromFavourite(it)
                }
                binding.btnAddFav.setImageResource(R.drawable.ic_heart_light)
                binding.btnAddFav.tint(R.color.colorWhite)
            }
            FavouriteReceiver.broadcast(requireContext().applicationContext, !isFav)
        }

        binding.btnLockScreen.onClick {
            openBatterySaverMode()
        }

        binding.btnClosePanel.onClick {
            collapsePlayer()
        }

        binding.btnPlayNext.onClick {
            viewModel.playNext()
        }

        binding.btnPlayPrevious.onClick {
            viewModel.playPrevious()
        }

        binding.btnShowQueueFull.onClick {
            showQueue()
        }

        binding.btnPlayOption.onClick {
            // Get next state
            val nextSort = UserPrefs.getSort().next()
            binding.btnPlayOption.setImageResource(nextSort.icon)
            UserPrefs.saveSort(nextSort)
        }


        binding.lockScreenView.doOnSlideComplete {
            checkLockScreen(false)
        }

        binding.miniPlayerView.doOnClickPlayPause {
            onClickPlayPause()
        }
        binding.miniPlayerView.doOnClickShowQueue {
            showQueue()
        }
    }

    private fun observeViewModel() {
        observe(viewModel.queue) { items ->
        }
        observe(PlayerQueue) { video ->
            onVideoChanged(video)
            binding.lockScreenView.setCurrentTrack(video)
            bindServiceIfNecessary()
        }
        observe(PlaybackDuration) { elapsedSeconds ->
            if (!seekingDuration) {
                updateCurrentTrackTime(elapsedSeconds)
                PlayerQueue.value?.let { currentTrack ->
                    val totalSeconds = currentTrack.totalSeconds.toInt()
                    val progress =
                        if (totalSeconds > 0) (elapsedSeconds * 100 / currentTrack.totalSeconds).toInt()
                        else 0
                    binding.seekBarDuration.animateProgress(progress)
                    binding.miniPlayerView.updateProgress(progress)
                }
            }
        }

        observe(viewModel.isLiked) { isLiked ->
            if (isLiked) {
                binding.btnAddFav.setImageResource(R.drawable.ic_heart_solid)
                binding.btnAddFav.tint(R.color.colorAccent)
            } else {
                binding.btnAddFav.setImageResource(R.drawable.ic_heart_light)
                binding.btnAddFav.tint(R.color.colorWhite)
            }
        }
    }

    // TODO: move outside fragment
    private fun onClickPlayPause() {
        Log.d(TAG_SERVICE, "onClickPlayPause: service bound=$serviceBound")
        val oldState = PlaybackLiveData.value
        oldState?.let { playerState ->
            if (playerState == PlayerConstants.PlayerState.PLAYING) {
                PlayerQueue.pause()
            } else if (playerState == PlayerConstants.PlayerState.PAUSED) {
                PlayerQueue.resume()
            }
        }
    }

    private fun showQueue() {
        PlayerQueue.hideVideo()
        activity?.findViewById<ViewGroup>(R.id.queueFragmentContainer)?.isVisible = true
        val fragment = activity?.supportFragmentManager
            ?.findFragmentById(R.id.queueFragmentContainer) ?: QueueFragment()
        val fm = activity?.supportFragmentManager
        fm?.beginTransaction()?.replace(R.id.queueFragmentContainer, fragment)?.commit()
        (fragment as? QueueFragment)?.doOnClose {
            onQueueClosed()
        }
    }

    fun onQueueClosed() {
        binding.btnPlayOption.setImageResource(UserPrefs.getSort().icon)
    }

    fun openBatterySaverMode() {
        lifecycleScope.launchWhenResumed {
            Log.d(TAG_SERVICE, "openBatterySaverMode: PlayerView=$reusedPlayerView")
            val canWriteSettings = SystemSettings.canWriteSettings(requireContext())
                    && SystemSettings.canDrawOverApps(requireContext())
            if (!canWriteSettings) {
                // Show popup
                MaterialDialog(requireContext()).show {
                    message(R.string.battery_saver_mode_request_change_settings)
                    positiveButton(R.string.ok) {
                        activity?.let { activity ->
                            SystemSettings.enableSettingModification(
                                this@PlayerFragment,
                                PlayerFragment.RQ_CODE_WRITE_SETTINGS
                            )
                        }
                    }
                    negativeButton(R.string.cancel)
                    getActionButton(WhichButton.NEGATIVE).updateTextColor(Color.parseColor("#808184"))
                    window?.setType(windowOverlayTypeOrPhone)
                }
                return@launchWhenResumed
            }

            // A hacky solution to wait service binding
            var count = 0
            while (reusedPlayerView == null && count < 20 && isActive) {
                count++
                delay(50)
            }
            Log.d(TAG_SERVICE, "openBatterySaverMode: wait for $count cycles")
            checkLockScreen(true)
        }
    }

    private fun checkLockScreen(lock: Boolean) {
        Log.d(TAG_SERVICE, "checkLockScreen: $lock")
        binding.lockScreenView.isVisible = lock
        (activity as? MainActivity)?.isLocked = lock
        binding.lockScreenView.toggle(lock)
        if (lock) {
            binding.lockScreenView.acquirePlayer(reusedPlayerView)
        } else {
            showPlayerView("lock")
        }
        // Disable/Enable motion transition
        binding.motionLayout.getTransition(R.id.mainTransition).setEnable(!lock)
    }

    private fun onVideoChanged(track: MusicTrack) {
        binding.miniPlayerView.onTrackChanged(track)

        binding.txtTitle.ellipsize = null
        binding.txtTitle.text = track.title
        binding.artistName.text = track.title.substringBefore("-")
        lifecycleScope.launch {
            delay(500)
            binding.txtTitle.ellipsize = TextUtils.TruncateAt.MARQUEE
        }


        if (UserPrefs.isFav(track.youtubeId)) {
            binding.btnAddFav.setImageResource(R.drawable.ic_heart_solid)
            binding.btnAddFav.tint(R.color.colorAccent)
        } else {
            binding.btnAddFav.setImageResource(R.drawable.ic_heart_light)
            binding.btnAddFav.tint(R.color.colorWhite)
        }
        configureSeekBar(track)
    }

    @SuppressLint("SetTextI18n")
    private fun configureSeekBar(video: MusicTrack) {
        binding.txtDuration.text = video.durationFormatted
        binding.txtElapsedTime.text = "00:00"
        binding.seekBarDuration.progress = 0
        binding.miniPlayerView.updateProgress(0)
        binding.seekBarDuration.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    seekingDuration = true
                    val seconds = progress * video.totalSeconds.toInt() / 100
                    updateCurrentTrackTime(seconds)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                seekingDuration = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                val progress = seekBar.progress
                val seconds = progress * video.totalSeconds / 100
                PlayerQueue.seekTo(seconds * 1000)
                lifecycleScope.launch {
                    delay(500)
                    seekingDuration = false
                }
            }
        })
    }

    private fun updateCurrentTrackTime(elapsedSeconds: Int) {
        val minutes = elapsedSeconds / 60
        val seconds = elapsedSeconds % 60
        binding.txtElapsedTime.text = String.format("%d:%02d", minutes, seconds)
    }

    //region Motion Layout Transition
    private fun setupMotionLayout() {
        binding.motionLayout.addTransitionListener(object : TransitionAdapter() {
            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {
                if (endId != R.id.hidden && startId != R.id.hidden) {
                    Log.d(
                        TAG,
                        "onTransitionChange: ${transitionInfo()}, and startId=${stateName(startId)}, and endId=${
                            stateName(
                                endId
                            )
                        }"
                    )
                    (activity as? MainActivity)?.binding?.motionLayout?.progress = progress
                }
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                val parentMotionLayout = (activity as? MainActivity)?.binding?.motionLayout
                Log.d(
                    TAG,
                    "onTransitionCompleted: ${transitionInfo()}, and currentId=${stateName(currentId)}"
                )
                if (currentId == R.id.expanded) {
                    parentMotionLayout?.progress = 1.0f
                } else if (currentId == R.id.collapsed) {
                    parentMotionLayout?.progress = 0.0f
                } else if (currentId == R.id.hidden) {
                    Log.d(TAG, "onTransitionCompleted: Player hidden")
                    mediaController?.transportControls?.stop()
                }
            }

            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int
            ) {

            }
        })

        binding.newPager.addTransitionListener(object : TransitionAdapter() {
            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                if (currentId == R.id.swipedRight) {
                    viewModel.swipeRight()
                } else if (currentId == R.id.swipedLeft) {
                    viewModel.swipeLeft()
                }
            }
        })
    }

    //endregion
    fun showPlayerView(from: String) {
        Log.d(TAG_SERVICE, "showPlayerView: from $from")
        val playerView = reusedPlayerView ?: kotlin.run {
            Log.d(TAG_SERVICE, "showPlayerView: view not yet initialized")
            return
        }
        if (playerView.parent == binding.cardPager) kotlin.run {
            Log.d(TAG_SERVICE, "showPlayerView: view already shown")
            return
        }
        val oldParent = playerView.parent as? ViewGroup
        if (oldParent == null) {
            Log.d(TAG_SERVICE, "showPlayerView: old parent null")
        }
        oldParent?.removeView(playerView)
        binding.cardPager.addView(playerView, 0)
    }

    private fun onPlayMusicStateChanged(stateCompat: PlaybackStateCompat) {
        binding.miniPlayerView.onPlayMusicStateChanged(stateCompat)
        val state = stateCompat.state
        if (state == PlaybackStateCompat.STATE_PLAYING || state == PlaybackStateCompat.STATE_BUFFERING) {
            binding.btnPlayPauseMain.setImageResource(R.drawable.ic_pause)
            binding.lockScreenView.onPlayBackStateChanged()
        } else if (state == PlaybackStateCompat.STATE_PAUSED) {
            binding.btnPlayPauseMain.setImageResource(R.drawable.ic_play)
            binding.lockScreenView.onPlayBackStateChanged()
        } else if (state == PlaybackStateCompat.STATE_STOPPED) {
            hidePlayer()
        }
    }

    private fun checkToShowTipBatterySaver() {
        // it was mainView
        val parent = binding.root ?: return
        parent.post {
            if (parent.windowToken != null) {
                if (!UserPrefs.hasSeenToolTipBatterySaver() && isExpanded()) {
                    val tip = Tooltip.Builder(requireContext())
                        .styleId(R.style.TooltipLayoutStyle)
                        .anchor(binding.btnLockScreen)
                        .text(R.string.tool_tip_battery_saver)
                        .arrow(true)
                        .overlay(true)
                        .closePolicy(ClosePolicy.TOUCH_ANYWHERE_CONSUME)
                        .maxWidth(requireContext().dpToPixel(260f))
                        .floatingAnimation(Tooltip.Animation.SLOW)
                        .create()
                    tip.show(parent, Tooltip.Gravity.CENTER, true)
                    tip.doOnHidden {
                        UserPrefs.setSeenToolTipBatterySaver()
                    }
                }
            }
        }
    }

    /// Public API ///
    fun expandPlayer() {
        Log.d(TAG, "expandPlayer, ${transitionInfo()}")
        lifecycleScope.launchWhenResumed {
            binding.motionLayout.setTransition(R.id.mainTransition)
            binding.motionLayout.progress = 1f
            binding.motionLayout.transitionToState(R.id.expanded)

            // Make sure bottom bar is visible
            ensureBottomNavBarHidden()
        }
    }

    fun collapsePlayer() {
        lifecycleScope.launchWhenResumed {
            Log.d(TAG, "collapsePlayer, ${transitionInfo()}")
            binding.motionLayout.setTransition(R.id.mainTransition)
            binding.motionLayout.transitionToState(R.id.collapsed)
        }
    }

    fun isExpanded(): Boolean {
        return binding.motionLayout.currentState == R.id.expanded
    }

    fun isPlayerHidden(): Boolean {
        return binding.motionLayout.currentState == R.id.hidden
    }

    fun isCollapsed(): Boolean {
        return binding.motionLayout.currentState == R.id.collapsed
    }

    fun hidePlayer() {
        Log.d(TAG, "hidePlayer, ${transitionInfo()}")
        binding.motionLayout.setTransition(R.id.initialState)
        binding.motionLayout.progress = 0f
        binding.motionLayout.transitionToState(R.id.hidden)

        // Make sure bottom bar is visible
        ensureBottomNavBarVisible()
    }

    private fun ensureBottomNavBarVisible() {
        (activity as? MainActivity)?.binding?.motionLayout?.progress = 0f
    }

    private fun ensureBottomNavBarHidden() {
        (activity as? MainActivity)?.binding?.motionLayout?.progress = 1f
    }

    private fun ensurePlayerVisible() {
        if (isPlayerHidden()) {
            collapsePlayer()
        }
    }

    private fun stateName(id: Int): String {
        return when (id) {
            R.id.collapsed -> "Collapsed"
            R.id.expanded -> "Expanded"
            R.id.hidden -> "Hidden"
            else -> "Unknown"
        }
    }

    private fun transitionInfo(): String {
        return "State: (start,end, current) = (${stateName(binding.motionLayout.startState)},${
            stateName(
                binding.motionLayout.endState
            )
        },${
            stateName(
                binding.motionLayout.currentState
            )
        }) progress=${binding.motionLayout.progress}"
    }

    companion object {
        private const val RQ_CODE_WRITE_SETTINGS = 101
    }
}