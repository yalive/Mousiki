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
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.getActionButton
import com.cas.common.dpToPixel
import com.cas.common.extensions.isInPictureInPictureModeCompact
import com.cas.common.extensions.onClick
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.FragmentPlayerBinding
import com.cas.musicplayer.di.Injector
import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.player.TAG_PLAYER
import com.cas.musicplayer.player.extensions.toText
import com.cas.musicplayer.player.iconId
import com.cas.musicplayer.player.receiver.FavouriteReceiver
import com.cas.musicplayer.player.services.MusicPlayerService
import com.cas.musicplayer.player.services.PlaybackDuration
import com.cas.musicplayer.player.services.PlaybackLiveData
import com.cas.musicplayer.tmp.observe
import com.cas.musicplayer.ui.MainActivity
import com.cas.musicplayer.ui.base.adjustStatusBarWithTheme
import com.cas.musicplayer.ui.base.darkStatusBar
import com.cas.musicplayer.ui.bottomsheet.TrackOptionsFragment
import com.cas.musicplayer.ui.player.queue.QueueFragment
import com.cas.musicplayer.ui.player.view.animateProgress
import com.cas.musicplayer.ui.settings.TimerDialog
import com.cas.musicplayer.utils.*
import com.mousiki.shared.domain.models.*
import com.mousiki.shared.preference.UserPrefs
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.*
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import it.sephiroth.android.library.xtooltip.ClosePolicy
import it.sephiroth.android.library.xtooltip.Tooltip
import kotlinx.coroutines.*

/**
 ************************************
 * Created by Abdelhadi on 11/28/20.
 * Copyright © 2020 Mousiki
 ************************************
 */

class PlayerFragment : Fragment(R.layout.fragment_player) {
    private val binding by viewBinding(FragmentPlayerBinding::bind)
    private val viewModel by viewModel { Injector.playerViewModel }
    private var seekingDuration = false

    private var mediaController: MediaControllerCompat? = null
    private var playerService: MusicPlayerService? = null

    val reusedPlayerView: YouTubePlayerView?
        get() = playerService?.getPlayerView()

    private var serviceBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d(TAG_PLAYER, "onServiceDisconnected: PlayerFragment")
            playerService = null
            serviceBound = false
        }

        override fun onServiceConnected(name: ComponentName?, binder: IBinder) {
            Log.d(TAG_PLAYER, "onServiceConnected: PlayerFragment")
            val service = (binder as MusicPlayerService.ServiceBinder).service()
            playerService = service
            mediaController = MediaControllerCompat(requireContext(), service.mediaSession)
            mediaController?.registerCallback(mediaControllerCallback)
            mediaController?.playbackState?.let { onPlayMusicStateChanged(it) }
            showPlayerView()
        }

        override fun onBindingDied(name: ComponentName?) {
            Log.d(TAG_PLAYER, "onBindingDied: PlayerFragment")
            super.onBindingDied(name)
            unbindService()
            bindServiceIfNecessary()
        }

        override fun onNullBinding(name: ComponentName?) {
            Log.d(TAG_PLAYER, "onNullBinding: PlayerFragment")
            super.onNullBinding(name)
            unbindService()
            bindServiceIfNecessary()
        }
    }

    private val mediaControllerCallback = object : MediaControllerCompat.Callback() {

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            Log.d(TAG_PLAYER, "onPlaybackStateChanged: (${state?.toText()}) PlayerFragment")
            state?.let { onPlayMusicStateChanged(state) }
        }

        override fun onSessionDestroyed() {
            Log.d(TAG_PLAYER, "onSessionDestroyed: PlayerFragment")
            serviceBound = false
            bindServiceIfNecessary()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG_PLAYER, "onCreate: PlayerFragment")
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG_PLAYER, "onViewCreated: PlayerFragment")
        super.onViewCreated(view, savedInstanceState)
        setupView()
        observeViewModel()
        bindServiceIfNecessary()
    }

    override fun onStart() {
        Log.d(TAG_PLAYER, "onStart: PlayerFragment")
        super.onStart()
        VideoEmplacementLiveData.inApp()
        viewModel.prepareAds()
    }

    override fun onResume() {
        Log.d(TAG_PLAYER, "onResume: PlayerFragment")
        super.onResume()
        if (binding.lockScreenView.isVisible) {
            checkLockScreen(true)
        }

        // Make sure video is visible if service is bound
        if (requireActivity().isInPictureInPictureModeCompact) {
            (requireActivity() as MainActivity).showPipPlayerView()
        } else {
            showPlayerView()
        }
    }

    override fun onPause() {
        Log.d(TAG_PLAYER, "onPause: PlayerFragment")
        super.onPause()
        if (binding.lockScreenView.isVisible) {
            binding.lockScreenView.disableLock()
            binding.motionLayout.getTransition(R.id.mainTransition).setEnable(true)
        }
    }

    override fun onStop() {
        Log.d(TAG_PLAYER, "onStop: PlayerFragment")
        super.onStop()
        // Movable video
        if (PlaybackLiveData.isPlaying()) {
            VideoEmplacementLiveData.out()
        }
    }

    override fun onDestroyView() {
        Log.d(TAG_PLAYER, "onDestroyView: PlayerFragment")
        super.onDestroyView()
        unbindService()
    }

    private fun unbindService() {
        if (serviceBound) {
            serviceBound = false
            mediaController?.unregisterCallback(mediaControllerCallback)
            activity?.unbindService(serviceConnection)
            playerService = null
        }
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
            val resultBind = activity?.bindService(
                intent,
                serviceConnection,
                Context.BIND_IMPORTANT and Context.BIND_AUTO_CREATE
            )
            if (resultBind == true) {
                serviceBound = true
            }
        }
    }

    private fun setupView() {
        setupMotionLayout()
        binding.btnPlayOption.setImageResource(
            UserPrefs.getCurrentPlaybackSort().iconId(requireContext())
        )
        setUpUserEvents()
        observe(DeviceInset) { inset ->
            binding.fullScreenSwitchView.updatePadding(top = inset.top)
            binding.playbackControlsView.updatePadding(bottom = inset.bottom)

            // collapsed
            val constraintSet = binding.motionLayout.getConstraintSet(R.id.collapsed)
            constraintSet.setMargin(
                R.id.miniPlayerView,
                ConstraintSet.BOTTOM,
                inset.bottom + dpToPixel(56)
            )
        }
        binding.txtTitle.isSelected = true
    }

    private fun setUpUserEvents() {
        binding.btnPlayPauseMain.onClick {
            onClickPlayPause()
        }

        binding.btnSleepTimer.onClick {
            TimerDialog.present(childFragmentManager)
        }

        binding.btnShareVia.onClick {
            Utils.shareTrack(PlayerQueue.value, requireContext())
        }

        binding.btnAddFav.onClick {
            val isFav = UserPrefs.isFav(PlayerQueue.value?.id)
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
            val nextSort = UserPrefs.getCurrentPlaybackSort().next()
            binding.btnPlayOption.setImageResource(nextSort.iconId(requireContext()))
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

        binding.btnTrackOptions.onClick {
            val track = PlayerQueue.value ?: return@onClick
            TrackOptionsFragment.present(childFragmentManager, track)
        }
    }

    private fun observeViewModel() {
        observe(viewModel.queue) { items ->
            binding.miniPlayerView.showNoTrack(items.isEmpty())
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
                    val totalSeconds = currentTrack.durationToSeconds().toInt()
                    val progress =
                        if (totalSeconds > 0) (elapsedSeconds * 100 / currentTrack.durationToSeconds()).toInt()
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
        val playerState = PlaybackLiveData.value
        if (playerState == PlayerState.PLAYING) {
            PlayerQueue.pause()
        } else if (playerState == PlayerState.PAUSED || playerState == PlayerState.VIDEO_CUED || playerState == PlayerState.ENDED) {
            PlayerQueue.resume()
        } else {
            PlayerQueue.playCurrentTrack()
        }
    }

    private fun showQueue() {
        QueueFragment.present(requireActivity()) {
            binding.btnPlayOption.setImageResource(
                UserPrefs.getCurrentPlaybackSort().iconId(requireContext())
            )
            adjustStatusBarWithTheme()
        }
    }

    fun openBatterySaverMode() {
        lifecycleScope.launchWhenResumed {
            val canWriteSettings = SystemSettings.canWriteSettings(requireContext())
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
                }
                return@launchWhenResumed
            }

            // A hacky solution to wait service binding
            var count = 0
            while (reusedPlayerView == null && count < 20 && isActive) {
                count++
                delay(50)
            }
            checkLockScreen(true)
        }
    }

    private fun checkLockScreen(lock: Boolean) {
        binding.lockScreenView.isVisible = lock
        binding.lockScreenView.toggle(lock)
        if (lock) {
            binding.lockScreenView.acquirePlayer(reusedPlayerView)
        } else {
            showPlayerView()
        }
        // Disable/Enable motion transition
        binding.motionLayout.getTransition(R.id.mainTransition).setEnable(!lock)
    }

    private fun onVideoChanged(track: Track) {
        val isLocalSong = track is LocalSong
        if (isLocalSong) {
            binding.poweredByValue.setText(R.string.app_name)
            binding.imgAudio.loadTrackImage(track, false)
        } else {
            binding.poweredByValue.setText(R.string.label_developed_with_youtube_part2)
        }
        binding.miniPlayerView.onTrackChanged(track)

        binding.txtTitle.ellipsize = null
        binding.txtTitle.text = track.title
        binding.artistName.text = track.artistName
        viewLifecycleOwner.lifecycleScope.launch {
            delay(500)
            binding.txtTitle.ellipsize = TextUtils.TruncateAt.MARQUEE
        }


        if (UserPrefs.isFav(track.id)) {
            binding.btnAddFav.setImageResource(R.drawable.ic_heart_solid)
            binding.btnAddFav.tint(R.color.colorAccent)
        } else {
            binding.btnAddFav.setImageResource(R.drawable.ic_heart_light)
            binding.btnAddFav.tint(R.color.colorWhite)
        }
        configureSeekBar(track)

        // Show
        binding.imgAudio.isVisible = isLocalSong
        binding.btnLockScreen.isVisible = !isLocalSong
        binding.btnYoutube.alpha =
            if (track is YtbTrack) 1.0f else 0.0f // Not working: a motion layout trick!!
    }

    @SuppressLint("SetTextI18n")
    private fun configureSeekBar(video: Track) {
        binding.txtDuration.text = video.durationFormatted
        binding.txtElapsedTime.text = "00:00"
        binding.seekBarDuration.progress = 0
        binding.miniPlayerView.updateProgress(0)
        binding.seekBarDuration.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    seekingDuration = true
                    val seconds = progress * video.durationToSeconds().toInt() / 100
                    updateCurrentTrackTime(seconds)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                seekingDuration = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                val progress = seekBar.progress
                val seconds = progress * video.durationToSeconds() / 100
                PlayerQueue.seekTo(seconds * 1000)
                viewLifecycleOwner.lifecycleScope.launch {
                    delay(500)
                    seekingDuration = false
                }
            }
        })
    }

    private fun updateCurrentTrackTime(elapsedSeconds: Int) {
        val minutes = elapsedSeconds / 60
        val seconds = elapsedSeconds % 60
        binding.txtElapsedTime.text = String.format("%02d:%02d", minutes, seconds)
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
                (activity as? MainActivity)?.binding?.motionLayout?.progress = progress
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                val parentMotionLayout = (activity as? MainActivity)?.binding?.motionLayout
                if (currentId == R.id.expanded) {
                    parentMotionLayout?.progress = 1.0f
                } else if (currentId == R.id.collapsed) {
                    parentMotionLayout?.progress = 0.0f
                }
                if (currentId == R.id.expanded) {
                    darkStatusBar()
                } else {
                    adjustStatusBarWithTheme()
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
    fun showPlayerView() {
        val playerView = reusedPlayerView ?: return
        if (playerView.parent == binding.cardPager) return
        val oldParent = playerView.parent as? ViewGroup
        oldParent?.removeView(playerView)
        binding.cardPager.addView(playerView, 1)
    }

    private fun onPlayMusicStateChanged(stateCompat: PlaybackStateCompat) {
        val state = stateCompat.state
        binding.miniPlayerView.onPlayMusicStateChanged(state)
        if (state == PlaybackStateCompat.STATE_PLAYING || state == PlaybackStateCompat.STATE_BUFFERING) {
            binding.btnPlayPauseMain.setImageResource(R.drawable.ic_pause)
            binding.lockScreenView.onPlayBackStateChanged()
        } else if (state == PlaybackStateCompat.STATE_PAUSED) {
            binding.btnPlayPauseMain.setImageResource(R.drawable.ic_play)
            binding.lockScreenView.onPlayBackStateChanged()
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
            binding.motionLayout.setTransition(R.id.mainTransition)
            binding.motionLayout.transitionToState(R.id.collapsed)
        }
    }

    fun isExpanded(): Boolean {
        return binding.motionLayout.currentState == R.id.expanded
    }

    fun isCollapsed(): Boolean {
        return binding.motionLayout.currentState == R.id.collapsed
    }

    private fun ensureBottomNavBarVisible() {
        (activity as? MainActivity)?.binding?.motionLayout?.progress = 0f
    }

    private fun ensureBottomNavBarHidden() {
        (activity as? MainActivity)?.binding?.motionLayout?.progress = 1f
    }

    companion object {
        private const val RQ_CODE_WRITE_SETTINGS = 101
    }
}