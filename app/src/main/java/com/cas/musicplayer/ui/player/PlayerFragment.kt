package com.cas.musicplayer.ui.player

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
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
import androidx.core.os.postDelayed
import androidx.core.view.isVisible
import androidx.core.view.postDelayed
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
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

/**
 ************************************
 * Created by Abdelhadi on 11/28/20.
 * Copyright © 2020 Mousiki
 ************************************
 */
class PlayerFragment : Fragment(R.layout.fragment_player) {

    private lateinit var mainActivity: MainActivity

    private val binding by viewBinding(FragmentPlayerBinding::bind)
    private val viewModel by viewModel { injector.playerViewModel }
    private val handler = Handler()
    private var seekingDuration = false

    private var mediaController: MediaControllerCompat? = null
    private var playerService: MusicPlayerService? = null

    private var reusedPlayerView: YouTubePlayerView? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d(TAG, "onServiceDisconnected")
            playerService = null
        }

        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            Log.d(TAG, "onServiceConnected")
            if (binder is MusicPlayerService.ServiceBinder) {
                playerService = binder.service()
                val service = binder.service()
                mediaController = MediaControllerCompat(requireContext(), service.mediaSession)
                mediaController?.registerCallback(mediaControllerCallback)
                mediaController?.playbackState?.let { onPlayMusicStateChanged(it) }
                reusedPlayerView = service.getPlayerView()
                adjustPlayerPosition()
            }
        }
    }
    private val mediaControllerCallback = object : MediaControllerCompat.Callback() {
        override fun binderDied() {
            Log.d(TAG, "binderDied: ")
            super.binderDied()
            hidePlayer()
        }

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            Log.d(TAG, "onPlaybackStateChanged: ${state?.toText()}")
            state?.let { onPlayMusicStateChanged(state) }
        }

        override fun onSessionDestroyed() {
            Log.d(TAG, "onSessionDestroyed: ")
            hidePlayer()
            bindService()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainActivity = requireActivity() as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        observeViewModel()
    }

    override fun onStart() {
        super.onStart()
        bindService()
        mediaController?.playbackState?.let { onPlayMusicStateChanged(it) }
        viewModel.prepareAds()
    }

    private fun bindService() {
        val intent = Intent(requireContext(), MusicPlayerService::class.java)
        activity?.bindService(intent, serviceConnection, 0)
    }

    override fun onResume() {
        super.onResume()
        val serviceRunning = context?.isServiceRunning(MusicPlayerService::class.java) ?: false
        if (serviceRunning) {
            adjustPlayerPosition()
        }
    }

    override fun onStop() {
        super.onStop()
        mediaController?.unregisterCallback(mediaControllerCallback)
        activity?.unbindService(serviceConnection)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RQ_CODE_WRITE_SETTINGS) {
            val canWriteSettings = SystemSettings.canWriteSettings(requireContext())
                    && SystemSettings.canDrawOverApps(requireContext())
            if (canWriteSettings) {
                handler.postDelayed(500) {
                    // ensure video in center
                    expandPlayer()
                }
                openBatterySaverMode()
            }
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
            lockScreen(false)
        }

        binding.miniPlayerView.doOnClickPlayPause {
            onClickPlayPause()
        }
        binding.miniPlayerView.doOnClickShowQueue {
            showQueue()
        }
    }

    private val TAG = "PlayerFragment_pager"

    private fun adjustPlayControlsForItemAt(position: Int) {
        val alpha = if (viewModel.isAdsItem(position)) 0.0f else 1.0f
        binding.playbackControlsView.alpha = alpha
        binding.seekBarView.alpha = alpha
        binding.btnLockScreen.alpha = alpha
    }

    private fun observeViewModel() {
        observe(viewModel.queue) { items ->
        }
        observe(PlayerQueue) { video ->
            onVideoChanged(video)
            binding.lockScreenView.setCurrentTrack(video)
            adjustPlayerPosition()
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
            return
        }
        lockScreen(true)
    }

    private fun lockScreen(lock: Boolean) {
        //binding.mainView.isVisible = !lock
        mainActivity.isLocked = lock
        binding.lockScreenView.toggle(lock)
        if (lock) {
            playerService?.getPlayerView()?.let { playerView ->
                binding.lockScreenView.postDelayed(300) {
                    binding.lockScreenView.acquirePlayer(playerView)
                }
            }
        }
    }


    private fun onVideoChanged(track: MusicTrack) {
        binding.miniPlayerView.onTrackChanged(track)

        binding.txtTitle.ellipsize = null
        binding.txtTitle.text = track.title
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
                handler.postDelayed(500) {
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
                    (activity as? MainActivity)?.binding?.motionLayout?.progress = progress
                    adjustPlayerPosition()
                }
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                if (currentId == R.id.expanded) {
                    (activity as? MainActivity)?.binding?.motionLayout?.progress = 1.0f
                    adjustPlayerPosition()
                } else if (currentId == R.id.collapsed) {
                    (activity as? MainActivity)?.binding?.motionLayout?.progress = 0.0f
                    adjustPlayerPosition()
                } else if (currentId == R.id.hidden) {
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
                    viewModel.playPrevious()
                } else if (currentId == R.id.swipedLeft) {
                    viewModel.playNext()
                }
            }
        })
    }

    //endregion

    fun adjustPlayerPosition() {
        val playerView = reusedPlayerView ?: return
        if (playerView.parent == binding.cardPager) return
        val oldParent = playerView.parent as? ViewGroup
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
                if (!UserPrefs.hasSeenToolTipBatterySaver() && mainActivity.isBottomPanelExpanded()) {
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


    fun onExitFullScreen() {

    }


    /// Public API ///
    fun expandPlayer() {
        binding.motionLayout.transitionToState(R.id.expanded)
    }

    fun collapsePlayer() {
        binding.motionLayout.transitionToState(R.id.collapsed)
        adjustPlayerPosition()
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
        binding.motionLayout.transitionToState(R.id.hidden)
    }

    private fun currentState(): String {
        return when (binding.motionLayout.currentState) {
            R.id.collapsed -> "Collapsed: progress=${binding.motionLayout.progress}"
            R.id.expanded -> "Expanded: progress=${binding.motionLayout.progress}"
            R.id.hidden -> "Hidden: progress=${binding.motionLayout.progress}"
            else -> "Unknown(${binding.motionLayout.currentState}): progress=${binding.motionLayout.progress}"
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


    companion object {
        private const val RQ_CODE_WRITE_SETTINGS = 101
    }
}