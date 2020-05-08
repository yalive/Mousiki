package com.cas.musicplayer.ui.player


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
import android.widget.RelativeLayout
import android.widget.SeekBar
import androidx.core.os.bundleOf
import androidx.core.os.postDelayed
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.getActionButton
import com.cas.common.extensions.observe
import com.cas.common.extensions.onClick
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.di.injector.injector
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.domain.model.durationToSeconds
import com.cas.musicplayer.player.EmplacementFullScreen
import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.player.VideoEmplacement
import com.cas.musicplayer.player.services.FavouriteReceiver
import com.cas.musicplayer.player.services.MusicPlayerService
import com.cas.musicplayer.player.services.PlaybackDuration
import com.cas.musicplayer.player.services.PlaybackLiveData
import com.cas.musicplayer.ui.MainActivity
import com.cas.musicplayer.ui.playlist.create.AddTrackToPlaylistFragment
import com.cas.musicplayer.utils.*
import com.crashlytics.android.Crashlytics
import com.google.android.gms.ads.AdRequest
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.android.synthetic.main.fragment_player.*
import kotlinx.coroutines.launch
import java.util.concurrent.Executors


class PlayerFragment : Fragment(), SlidingUpPanelLayout.PanelSlideListener {

    lateinit var mainActivity: MainActivity
    private val viewModel by viewModel { injector.playerViewModel }
    private val handler = Handler()
    private var mediaController: MediaControllerCompat? = null
    private var btnFullScreen: ImageButton? = null
    private var btnPlayPause: ImageButton? = null
    private var btnPlayPauseMain: ImageButton? = null
    private var imgBlured: ImageView? = null
    private var lockScreenView: LockScreenView? = null
    private var queueFragment: SlideUpPlaylistFragment? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
        }

        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            if (binder is MusicPlayerService.ServiceBinder) {
                val service = binder.service()
                mediaController = MediaControllerCompat(requireContext(), service.mediaSession)
                mediaController?.registerCallback(mediaControllerCallback)
                mediaController?.playbackState?.let { onPlayMusicStateChanged(it) }
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
            queueFragment?.dismiss()
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
        btnPlayPause = view?.findViewById(R.id.btnPlayPause)
        btnPlayPauseMain = view?.findViewById(R.id.btnPlayPauseMain)
        lockScreenView = view?.findViewById(R.id.lockScreenView)
        imgBlured = view?.findViewById(R.id.imgBlured)

        mainActivity = requireActivity() as MainActivity
        mainActivity.slidingPaneLayout.addPanelSlideListener(this)
        PlayerQueue.observe(this, Observer { video ->
            onVideoChanged(video)
            lockScreenView?.setCurrentTrack(video)
        })

        btnPlayPause?.onClick {
            onClickPlayPause()
        }

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

        adjustCenterViews()

        PlaybackDuration.observe(this, Observer { elapsedSeconds ->
            val minutes = (elapsedSeconds / 60).toInt()
            val seconds = (elapsedSeconds % 60).toInt()
            txtElapsedTime.text = String.format("%d:%02d", minutes, seconds)

            PlayerQueue.value?.let { currentTrack ->
                val progress = elapsedSeconds * 100 / currentTrack.durationToSeconds()
                seekBarDuration.progress = progress.toInt()
            }

        })

        btnShowQueue.onClick {
            showQueue()
        }

        btnShowQueueFull.onClick {
            showQueue()
        }

        antiDrag.onClick {
            if (mainActivity.slidingPaneLayout.panelState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                mainActivity.expandBottomPanel()
            }
        }

        btnPlayOption.setImageResource(UserPrefs.getSort().icon)

        btnPlayOption.onClick {
            // Get next state
            val nextSort = UserPrefs.getSort().next()
            btnPlayOption.setImageResource(nextSort.icon)
            UserPrefs.saveSort(nextSort)
        }

        btnFullScreen?.onClick {
            (requireActivity() as MainActivity).switchToLandscape()
            (requireActivity() as MainActivity).hideStatusBar()
            VideoEmplacementLiveData.fullscreen()
        }

        DeviceInset.observe(this, Observer { inset ->
            fullScreenSwitchView.updatePadding(top = inset.top)
            adjustCenterViews()
        })

        topBarView.onClick {
            mainActivity.expandBottomPanel()
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
            lockScreen(false)
        }

        if (viewModel.bannerAdOn()) {
            bannerAdView.loadAd(AdRequest.Builder().build())
        } else {
            bannerAdView.isVisible = false
        }
    }

    fun openBatterySaverMode() {
        val canWriteSettings = SystemSettings.canWriteSettings(requireContext())
        if (!canWriteSettings) {
            // Show popup
            MaterialDialog(requireContext()).show {
                message(R.string.battery_saver_mode_request_change_settings)
                positiveButton(R.string.ok) {
                    SystemSettings.enableSettingModification(requireActivity())
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
        mainView.alpha = slideOffset
        topBarView.alpha = 1 - slideOffset
    }

    override fun onPanelStateChanged(
        panel: View?,
        previousState: SlidingUpPanelLayout.PanelState?,
        newState: SlidingUpPanelLayout.PanelState?
    ) {
        btnFullScreen?.isEnabled = newState == SlidingUpPanelLayout.PanelState.EXPANDED
    }

    private fun showQueue() {
        queueFragment = SlideUpPlaylistFragment()
        queueFragment?.show(childFragmentManager, "BottomSheetFragment")
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

    private fun adjustCenterViews() {
        if (VideoEmplacementLiveData.value is EmplacementFullScreen) {
            return
        }
        val emplacementCenter = VideoEmplacement.center()
        val paramsTitle = txtTitleVideoCenter.layoutParams as RelativeLayout.LayoutParams
        val horizontalMargin = emplacementCenter.x

        paramsTitle.topMargin = emplacementCenter.y - requireActivity().dpToPixel(80f)
        paramsTitle.marginStart = horizontalMargin
        paramsTitle.leftMargin = horizontalMargin
        paramsTitle.marginEnd = horizontalMargin
        paramsTitle.rightMargin = horizontalMargin

        txtTitleVideoCenter.layoutParams = paramsTitle

        val paramsTxtYoutubeCopy = btnYoutube.layoutParams as RelativeLayout.LayoutParams
        paramsTxtYoutubeCopy.topMargin = paramsTitle.topMargin - requireActivity().dpToPixel(20f)
        paramsTxtYoutubeCopy.marginStart = paramsTitle.marginStart
        paramsTxtYoutubeCopy.leftMargin = paramsTitle.leftMargin
        paramsTxtYoutubeCopy.marginEnd = paramsTitle.marginEnd
        paramsTxtYoutubeCopy.rightMargin = paramsTitle.rightMargin

        btnYoutube.layoutParams = paramsTxtYoutubeCopy
    }

    private fun onVideoChanged(video: MusicTrack) {
        txtTitle.text = video.title
        txtTitleVideoCenter.text = video.title

        if (UserPrefs.isFav(video.youtubeId)) {
            btnAddFav.setImageResource(R.drawable.ic_heart_solid)
            btnAddFav.tint(R.color.colorAccent)
        } else {
            btnAddFav.setImageResource(R.drawable.ic_heart_light)
            btnAddFav.tint(R.color.colorWhite)
        }
        loadAndBlurImage(video)
        configureSeekBar(video)
    }

    private fun configureSeekBar(video: MusicTrack) {
        txtDuration.text = video.durationFormatted
        txtElapsedTime.text = "00:00"
        seekBarDuration.progress = 0
        seekBarDuration.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Nothing
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Nothing
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.progress?.let { progress ->
                    // Map from  (0,100) to (0,duration)
                    val seconds = progress * video.durationToSeconds() / 100
                    PlayerQueue.seekTo(seconds * 1000)
                }
            }
        })
    }

    private fun loadAndBlurImage(video: MusicTrack) {
        lifecycleScope.launch {
            try {
                val bitmap = imgBlured?.getBitmap(video.imgUrlDefault) ?: return@launch
                imgBlured?.setImageBitmap(BlurImage.fastblur(bitmap, 1f, 45))
            } catch (e: Exception) {
                Crashlytics.logException(e)
            }
        }
    }

    private fun lockScreen(lock: Boolean) {
        mainView.isVisible = !lock
        mainActivity.isLocked = lock
        lockScreenView?.toggle(lock)
    }

    private fun onPlayMusicStateChanged(stateCompat: PlaybackStateCompat) {
        val state = stateCompat.state
        if (state == PlaybackStateCompat.STATE_PLAYING || state == PlaybackStateCompat.STATE_BUFFERING) {
            btnPlayPause?.setImageResource(R.drawable.ic_pause)
            btnPlayPauseMain?.setImageResource(R.drawable.ic_pause)
            lockScreenView?.onPlayBackStateChanged()
        } else if (state == PlaybackStateCompat.STATE_PAUSED) {
            btnPlayPause?.setImageResource(R.drawable.ic_play)
            btnPlayPauseMain?.setImageResource(R.drawable.ic_play)
            lockScreenView?.onPlayBackStateChanged()
        } else if (state == PlaybackStateCompat.STATE_STOPPED) {
            mainActivity.hideBottomPanel()
        }
    }
}
