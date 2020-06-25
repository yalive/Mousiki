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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import androidx.core.os.bundleOf
import androidx.core.os.postDelayed
import androidx.core.view.isVisible
import androidx.core.view.postDelayed
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.viewpager2.widget.ViewPager2
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.getActionButton
import com.cas.common.extensions.observe
import com.cas.common.extensions.onClick
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.di.injector.injector
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.player.services.FavouriteReceiver
import com.cas.musicplayer.player.services.MusicPlayerService
import com.cas.musicplayer.player.services.PlaybackDuration
import com.cas.musicplayer.player.services.PlaybackLiveData
import com.cas.musicplayer.ui.MainActivity
import com.cas.musicplayer.ui.home.model.toDisplayedVideoItem
import com.cas.musicplayer.ui.player.queue.QueueFragment
import com.cas.musicplayer.ui.playlist.create.AddTrackToPlaylistFragment
import com.cas.musicplayer.ui.popular.SongsDiffUtil
import com.cas.musicplayer.utils.*
import com.crashlytics.android.Crashlytics
import com.google.android.gms.ads.AdRequest
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.COLLAPSED
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.EXPANDED
import it.sephiroth.android.library.xtooltip.ClosePolicy
import it.sephiroth.android.library.xtooltip.Tooltip
import kotlinx.android.synthetic.main.fragment_player.*
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

const val TAG = "PlayerFragment.Log"

class PlayerFragment : Fragment(), SlidingUpPanelLayout.PanelSlideListener {

    private lateinit var mainActivity: MainActivity
    private val viewModel by viewModel { injector.playerViewModel }
    private val handler = Handler()
    private var mediaController: MediaControllerCompat? = null
    private var btnFullScreen: ImageButton? = null
    private var btnPlayOption: ImageButton? = null
    private var mainView: ViewGroup? = null
    private var btnPlayPauseMain: ImageButton? = null
    private var imgBlured: ImageView? = null
    private var lockScreenView: LockScreenView? = null
    private var seekingDuration = false

    private var playerService: MusicPlayerService? = null
    private lateinit var playerVideosAdapter: PlayerVideosAdapter

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            playerService = null
        }

        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            if (binder is MusicPlayerService.ServiceBinder) {
                playerService = binder.service()
                val service = binder.service()
                mediaController = MediaControllerCompat(requireContext(), service.mediaSession)
                mediaController?.registerCallback(mediaControllerCallback)
                mediaController?.playbackState?.let { onPlayMusicStateChanged(it) }
                playerVideosAdapter.reusedPlayerView = service.getPlayerView()
            }
        }
    }

    private val mediaControllerCallback = object : MediaControllerCompat.Callback() {
        override fun binderDied() {
            super.binderDied()
            mainActivity.hideBottomPanel()
        }

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            state?.let { onPlayMusicStateChanged(state) }
        }

        override fun onSessionDestroyed() {
            mainActivity.hideBottomPanel()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_player, container, false)
        return view
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(requireContext(), MusicPlayerService::class.java)
        activity?.bindService(intent, serviceConnection, 0)
        mediaController?.playbackState?.let { onPlayMusicStateChanged(it) }
        val serviceRunning = context?.isServiceRunning(MusicPlayerService::class.java) ?: false
        if (!serviceRunning) {
            mainActivity.hideBottomPanel()
        }
    }

    override fun onStop() {
        super.onStop()
        mediaController?.unregisterCallback(mediaControllerCallback)
        activity?.unbindService(serviceConnection)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        btnFullScreen = view?.findViewById(R.id.btnFullScreen)
        btnPlayOption = view?.findViewById(R.id.btnPlayOption)
        mainView = view?.findViewById(R.id.mainView)
        btnPlayPauseMain = view?.findViewById(R.id.btnPlayPauseMain)
        lockScreenView = view?.findViewById(R.id.lockScreenView)
        imgBlured = view?.findViewById(R.id.imgBlured)

        mainActivity = requireActivity() as MainActivity
        mainActivity.slidingPaneLayout.addPanelSlideListener(this)

        setupCenterViewPager()
        PlayerQueue.observe(viewLifecycleOwner, Observer { video ->
            onVideoChanged(video)
            lockScreenView?.setCurrentTrack(video)

            val newItems = PlayerQueue.queue?.map { it.toDisplayedVideoItem() } ?: emptyList()
            val diffCallback = SongsDiffUtil(playerVideosAdapter.videos, newItems)
            playerVideosAdapter.submitList(newItems, diffCallback)
            viewPager.post {
                viewPager.setCurrentItem(PlayerQueue.indexOfCurrent(), false)
            }
        })

        btnPlayPauseMain?.onClick {
            onClickPlayPause()
        }

        btnShareVia.onClick {
            Utils.shareWithDeepLink(PlayerQueue.value, mContext = mainActivity)
        }

        btnAddToPlaylist.onClick {
            val musicTrack = PlayerQueue.value ?: return@onClick
            (requireActivity() as? MainActivity)?.collapseBottomPanel()
            val currentDestinationId = findNavController().currentDestination?.id
            if (currentDestinationId == R.id.addTrackToPlaylistFragment
                || currentDestinationId == R.id.createPlaylistFragment
            ) return@onClick
            handler.postDelayed(500) {
                val navOptions = navOptions {
                    anim {
                        enter = R.anim.fad_in
                        exit = R.anim.fad_out
                    }
                }
                findNavController().navigate(
                    R.id.addTrackToPlaylistFragment, bundleOf(
                        AddTrackToPlaylistFragment.EXTRAS_TRACK to musicTrack,
                        AddTrackToPlaylistFragment.EXTRAS_CURRENT_DESTINATION to currentDestinationId
                    ), navOptions
                )
            }
        }
        btnAddFav.onClick {
            val isFav = UserPrefs.isFav(PlayerQueue.value?.youtubeId)
            if (!isFav) {
                Executors.newSingleThreadExecutor().execute {
                    val musicTrack = PlayerQueue.value
                    musicTrack?.let {
                        viewModel.makeSongAsFavourite(it)
                    }
                }
                btnAddFav.setImageResource(R.drawable.ic_heart_solid)
                btnAddFav.tint(R.color.colorAccent)
            } else {
                val musicTrack = PlayerQueue.value
                musicTrack?.let {
                    viewModel.removeSongFromFavourite(it)
                }
                btnAddFav.setImageResource(R.drawable.ic_heart_light)
                btnAddFav.tint(R.color.colorWhite)
            }
            FavouriteReceiver.broadcast(requireContext().applicationContext, !isFav)
        }

        btnLockScreen.onClick {
            openBatterySaverMode()
        }

        btnClosePanel.onClick {
            mainActivity.collapseBottomPanel()
        }

        btnPlayNext.onClick {
            PlayerQueue.playNextTrack()
        }

        btnPlayPrevious.onClick {
            PlayerQueue.playPreviousTrack()
        }

        PlaybackDuration.observe(viewLifecycleOwner, Observer { elapsedSeconds ->
            if (!seekingDuration) {
                updateCurrentTrackTime(elapsedSeconds)
                PlayerQueue.value?.let { currentTrack ->
                    val totalSeconds = currentTrack.totalSeconds.toInt()
                    val progress =
                        if (totalSeconds > 0) (elapsedSeconds * 100 / currentTrack.totalSeconds).toInt()
                        else 0
                    seekBarDuration.animateProgress(progress)
                    miniPlayerView.updateProgress(progress)
                }
            }
        })

        btnShowQueueFull.onClick {
            showQueue()
        }

        antiDrag.onClick {
            if (mainActivity.slidingPaneLayout.panelState == COLLAPSED) {
                mainActivity.expandBottomPanel()
            }
        }

        btnPlayOption?.setImageResource(UserPrefs.getSort().icon)

        btnPlayOption?.onClick {
            // Get next state
            val nextSort = UserPrefs.getSort().next()
            btnPlayOption?.setImageResource(nextSort.icon)
            UserPrefs.saveSort(nextSort)
        }

        btnFullScreen?.onClick {
            (requireActivity() as MainActivity).switchToLandscape()
            (requireActivity() as MainActivity).hideStatusBar()
            VideoEmplacementLiveData.fullscreen()
        }

        DeviceInset.observe(viewLifecycleOwner, Observer { inset ->
            fullScreenSwitchView.updatePadding(top = inset.top)
        })

        miniPlayerView.onClick {
            mainActivity.expandBottomPanel()
        }
        miniPlayerView.doOnClickPlayPause {
            onClickPlayPause()
        }
        miniPlayerView.doOnClickShowQueue {
            showQueue()
        }
        observe(viewModel.isLiked) { isLiked ->
            if (isLiked) {
                btnAddFav.setImageResource(R.drawable.ic_heart_solid)
                btnAddFav.tint(R.color.colorAccent)
            } else {
                btnAddFav.setImageResource(R.drawable.ic_heart_light)
                btnAddFav.tint(R.color.colorWhite)
            }
        }
        lockScreenView?.doOnSlideComplete {
            playerVideosAdapter.notifyItemChanged(viewPager.currentItem)
            lockScreen(false)
        }

        if (viewModel.bannerAdOn()) {
            bannerAdView.loadAd(AdRequest.Builder().build())
        } else {
            bannerAdView.isVisible = false
        }
    }

    override fun onResume() {
        super.onResume()
        acquirePlayerIfNeeded()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RQ_CODE_WRITE_SETTINGS) {
            val canWriteSettings = SystemSettings.canWriteSettings(requireContext())
                    && SystemSettings.canDrawOverApps(requireContext())
            if (canWriteSettings) {
                handler.postDelayed(500) {
                    // ensure video in center
                    (activity as? MainActivity)?.expandBottomPanel()
                }
                openBatterySaverMode()
            }
        }
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
                            RQ_CODE_WRITE_SETTINGS
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

    override fun onPanelSlide(panel: View?, slideOffset: Float) {
        mainView?.alpha = slideOffset
        miniPlayerView.alpha = 1 - slideOffset
    }

    override fun onPanelStateChanged(
        panel: View?,
        previousState: SlidingUpPanelLayout.PanelState?,
        newState: SlidingUpPanelLayout.PanelState?
    ) {
        btnFullScreen?.isEnabled = newState == EXPANDED
        if (newState == EXPANDED) {
            checkToShowTipBatterySaver()
            playerVideosAdapter.notifyDataSetChanged()
        } else if (newState == COLLAPSED) {
            val reusedPlayerView = playerVideosAdapter.reusedPlayerView
            reusedPlayerView?.let {
                miniPlayerView.acquirePlayer(reusedPlayerView)
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

    private fun onVideoChanged(track: MusicTrack) {
        miniPlayerView.onTrackChanged(track)

        if (UserPrefs.isFav(track.youtubeId)) {
            btnAddFav.setImageResource(R.drawable.ic_heart_solid)
            btnAddFav.tint(R.color.colorAccent)
        } else {
            btnAddFav.setImageResource(R.drawable.ic_heart_light)
            btnAddFav.tint(R.color.colorWhite)
        }
        loadAndBlurImage(track)
        configureSeekBar(track)
    }

    @SuppressLint("SetTextI18n")
    private fun configureSeekBar(video: MusicTrack) {
        txtDuration.text = video.durationFormatted
        txtElapsedTime.text = "00:00"
        seekBarDuration.progress = 0
        miniPlayerView.updateProgress(0)
        seekBarDuration.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
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
        txtElapsedTime.text = String.format("%d:%02d", minutes, seconds)
    }

    private fun loadAndBlurImage(video: MusicTrack) {
        lifecycleScope.launch {
            try {
                val bitmap = imgBlured?.getBitmap(video.imgUrlDefault, 500) ?: return@launch
                imgBlured?.updateBitmap(BlurImage.fastblur(bitmap, 0.1f, 50))
            } catch (e: Exception) {
                Crashlytics.logException(e)
            } catch (error: OutOfMemoryError) {
                Crashlytics.logException(error)
            }
        }
    }

    private fun lockScreen(lock: Boolean) {
        mainView?.isVisible = !lock
        mainActivity.isLocked = lock
        lockScreenView?.toggle(lock)
        if (lock) {
            playerService?.getPlayerView()?.let { playerView ->
                lockScreenView?.postDelayed(300) {
                    lockScreenView?.acquirePlayer(playerView)
                }
            }
        }
    }

    private fun onPlayMusicStateChanged(stateCompat: PlaybackStateCompat) {
        miniPlayerView.onPlayMusicStateChanged(stateCompat)
        val state = stateCompat.state
        if (state == PlaybackStateCompat.STATE_PLAYING || state == PlaybackStateCompat.STATE_BUFFERING) {
            btnPlayPauseMain?.setImageResource(R.drawable.ic_pause)
            lockScreenView?.onPlayBackStateChanged()
        } else if (state == PlaybackStateCompat.STATE_PAUSED) {
            btnPlayPauseMain?.setImageResource(R.drawable.ic_play)
            lockScreenView?.onPlayBackStateChanged()
        } else if (state == PlaybackStateCompat.STATE_STOPPED) {
            mainActivity.hideBottomPanel()
        }
    }

    private fun checkToShowTipBatterySaver() {
        val parent = mainView ?: return
        parent.post {
            if (parent.windowToken != null) {
                if (!UserPrefs.hasSeenToolTipBatterySaver() && mainActivity.isBottomPanelExpanded()) {
                    val tip = Tooltip.Builder(requireContext())
                        .styleId(R.style.TooltipLayoutStyle)
                        .anchor(btnLockScreen)
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

    fun onQueueClosed() {
        btnPlayOption?.setImageResource(UserPrefs.getSort().icon)
    }

    private fun acquirePlayerIfNeeded() {
        val panelState = mainActivity.slidingPaneLayout.panelState
        if (panelState == EXPANDED) {
            val currentItem = viewPager.currentItem
            if (currentItem >= 0) {
                playerVideosAdapter.notifyItemChanged(currentItem)
            }
        } else if (panelState == COLLAPSED) {
            val reusedPlayerView = playerVideosAdapter.reusedPlayerView
            reusedPlayerView?.let {
                miniPlayerView.post {
                    miniPlayerView.acquirePlayer(reusedPlayerView)
                }
            }
        }
    }


    /* Indicate if a swipe is initiated by user or programmatically */
    private val scrollEvents = mutableListOf<Int>()
    private fun setupCenterViewPager() {

        playerVideosAdapter = PlayerVideosAdapter(viewPager)
        viewPager.adapter = playerVideosAdapter
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val swipeByUser =
                    !(scrollEvents.isEmpty() || !scrollEvents.contains(ViewPager2.SCROLL_STATE_DRAGGING))
                viewPager.postDelayed(300) {
                    playerVideosAdapter.notifyItemChanged(position)
                    playerVideosAdapter.notifyItemChanged(viewModel.currentPage)
                    viewModel.currentPage = position

                    if (swipeByUser) {
                        PlayerQueue.playTrackAt(position)
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
                scrollEvents.add(state)
                if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    scrollEvents.clear()
                }
            }
        })
    }

    fun onExitFullScreen() {
        playerVideosAdapter.notifyDataSetChanged()
    }

    companion object {
        private const val RQ_CODE_WRITE_SETTINGS = 101
    }
}
