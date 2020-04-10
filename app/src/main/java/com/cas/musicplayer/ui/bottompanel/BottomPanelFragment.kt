package com.cas.musicplayer.ui.bottompanel


import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.SeekBar
import androidx.core.os.bundleOf
import androidx.core.os.postDelayed
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.cas.common.dpToPixel
import com.cas.common.extensions.gone
import com.cas.common.extensions.invisible
import com.cas.common.extensions.onClick
import com.cas.common.extensions.visible
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.di.injector.injector
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.domain.model.durationToSeconds
import com.cas.musicplayer.player.EmplacementFullScreen
import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.player.VideoEmplacement
import com.cas.musicplayer.player.services.PlaybackDuration
import com.cas.musicplayer.player.services.PlaybackLiveData
import com.cas.musicplayer.ui.MainActivity
import com.cas.musicplayer.ui.home.view.InsetSlidingPanelView
import com.cas.musicplayer.ui.playlist.create.AddTrackToPlaylistFragment
import com.cas.musicplayer.utils.*
import com.ncorti.slidetoact.SlideToActView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.fragment_bottom_panel.*
import java.util.concurrent.Executors


class BottomPanelFragment : Fragment(),
    SlidingUpPanelLayout.PanelSlideListener,
    SlideToActView.OnSlideCompleteListener,
    View.OnTouchListener {

    var dialogBottomShet: SlideUpPlaylistFragment? = null
    lateinit var mainActivity: MainActivity
    private var visible = true
    private val viewModel by viewModel { injector.bottomPanelViewModel }
    private val handler = Handler()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bottom_panel, container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainActivity = requireActivity() as MainActivity
        mainActivity.slidingPaneLayout.addPanelSlideListener(this)
        PlayerQueue.observe(this, Observer { video ->
            onVideoChanged(video)
        })

        btnPlayPause.setOnClickListener {
            onClickPlayPause()
        }

        btnPlayPauseMain.setOnClickListener {
            onClickPlayPause()
        }

        btnShareVia.setOnClickListener {
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
        btnAddFav.setOnClickListener {
            if (!UserPrefs.isFav(PlayerQueue.value?.youtubeId)) {
                Executors.newSingleThreadExecutor().execute {
                    val musicTrack = PlayerQueue.value
                    musicTrack?.let {
                        viewModel.makeSongAsFavourite(it)
                    }
                }
                UserPrefs.saveFav(PlayerQueue.value?.youtubeId, true)
                btnAddFav.setImageResource(R.drawable.ic_favorite_added_24dp)
            } else {
                val musicTrack = PlayerQueue.value
                musicTrack?.let {
                    viewModel.removeSongFromFavourite(it)
                }
                UserPrefs.saveFav(PlayerQueue.value?.youtubeId, false)
                btnAddFav.setImageResource(R.drawable.ic_favorite_border)
            }
        }

        btnLockScreen.setOnClickListener {
            lockScreen(true)
        }

        slideToUnlock.onSlideCompleteListener = this
        PlaybackLiveData.observe(this, Observer {
            if (it == PlayerConstants.PlayerState.PAUSED) {
                btnPlayPause.setImageResource(R.drawable.ic_play)
                btnPlayPauseMain.setImageResource(R.drawable.ic_play)
            } else if (it == PlayerConstants.PlayerState.PLAYING) {
                btnPlayPause.setImageResource(R.drawable.ic_pause)
                btnPlayPauseMain.setImageResource(R.drawable.ic_pause)
            }
        })

        btnClosePanel.setOnClickListener {
            mainActivity.collapseBottomPanel()
        }

        btnPlayNext.setOnClickListener {
            PlayerQueue.playNextTrack()
        }

        btnPlayPrevious.setOnClickListener {
            PlayerQueue.playPreviousTrack()
        }

        mainView.setOnTouchListener(this)

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

        btnShowQueue.setOnClickListener {
            showQueue()
        }

        btnShowQueueFull.setOnClickListener {
            showQueue()
        }

        antiDrag.setOnClickListener {
            if (mainActivity.slidingPaneLayout.panelState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                mainActivity.expandBottomPanel()
            }
        }

        btnPlayOption.setImageResource(UserPrefs.getSort().icon)

        btnPlayOption.setOnClickListener {
            // Get next state
            val nextSort = UserPrefs.getSort().next()
            btnPlayOption.setImageResource(nextSort.icon)
            UserPrefs.saveSort(nextSort)
        }

        btnFullScreen.setOnClickListener {
            (requireActivity() as MainActivity).switchToLandscape()
            (requireActivity() as MainActivity).hideStatusBar()
            VideoEmplacementLiveData.fullscreen()
        }

        DeviceInset.observe(this, Observer { inset ->
            fullScreenSwitchView.updatePadding(
                top = inset.top + dpToPixel(
                    8f,
                    requireContext()
                ).toInt()
            )
            adjustCenterViews()
        })

        topBarView.onClick {
            mainActivity.expandBottomPanel()
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event!!.action == MotionEvent.ACTION_DOWN) {
            if (visible && mainActivity.isLocked) {
                playbackControlsView.invisible()
                slideToUnlock.invisible()
                txtTitleVideoLock.invisible()
                visible = false
            } else if (!visible && mainActivity.isLocked) {
                playbackControlsView.visible()
                slideToUnlock.visible()
                txtTitleVideoLock.visible()
                visible = true
            }
        }
        return true
    }

    override fun onSlideComplete(view: SlideToActView) {
        lockScreen(false)
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
        btnFullScreen.isEnabled = newState == SlidingUpPanelLayout.PanelState.EXPANDED
    }

    private fun showQueue() {
        dialogBottomShet = SlideUpPlaylistFragment()
        dialogBottomShet?.show(childFragmentManager, "BottomSheetFragment")
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
        txtTitleVideoLock.text = video.title

        if (UserPrefs.isFav(video.youtubeId)) {
            btnAddFav.setImageResource(R.drawable.ic_favorite_added_24dp)
        } else {
            btnAddFav.setImageResource(R.drawable.ic_favorite_border)
        }
        loadAndBlureImage(video)
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
                    PlayerQueue.seekTo(seconds)
                }
            }
        })
    }

    private fun loadAndBlureImage(video: MusicTrack) {
        val target = object : Target {
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                // Nothing
            }

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                // Nothing
            }

            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                try {
                    bitmap?.let {
                        imgBlured.setImageBitmap(BlurImage.fastblur(bitmap, 1f, 45))
                    }
                } catch (e: OutOfMemoryError) {
                }
            }
        }

        imgBlured.tag = target
        Picasso.get().load(video.imgUrl).into(target)
    }

    private fun lockScreen(lock: Boolean) {
        if (lock) {
            val panelView =
                requireActivity().findViewById<InsetSlidingPanelView>(R.id.sliding_layout)
            panelView.updatePadding(top = 0)
            setFullscreen(mainActivity)
            fullScreenSwitchView.gone()
            btnYoutube.gone()
            favView.gone()
            seekBarView.gone()
            btnShowQueueFull.gone()
            btnPlayOption.gone()
            imgBlured.gone()
            slideToUnlock.visible()
            txtTitleVideoLock.visible()
            txtTitleVideoCenter.gone()
            mainView.setBackgroundColor(Color.parseColor("#000000"))
            mainActivity.slidingPaneLayout.isTouchEnabled = false
            mainActivity.isLocked = true

            handler.postDelayed(1_000) {
                panelView.updatePadding(top = 0)
            }
        } else {
            exitFullscreen(mainActivity)
            fullScreenSwitchView.visible()
            btnYoutube.visible()
            favView.visible()
            seekBarView.visible()
            btnShowQueueFull.visible()
            btnPlayOption.visible()
            imgBlured.visible()
            slideToUnlock.gone()
            txtTitleVideoLock.gone()
            txtTitleVideoCenter.visible()
            slideToUnlock.resetSlider()
            mainView.setBackgroundColor(resources.getColor(android.R.color.transparent))
            mainActivity.slidingPaneLayout.isTouchEnabled = true
            mainActivity.isLocked = false
        }
    }

    private fun setFullscreen(activity: Activity) {
        var flags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_FULLSCREEN
        flags = flags or (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        activity.window.decorView.systemUiVisibility = flags
    }

    private fun exitFullscreen(activity: Activity) {
        activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
    }
}
