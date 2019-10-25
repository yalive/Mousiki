package com.cas.musicplayer.ui.bottompanel


import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.RelativeLayout
import android.widget.SeekBar
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.cas.musicplayer.R
import com.cas.musicplayer.data.enteties.MusicTrack
import com.cas.musicplayer.data.enteties.MusicTrackRoomDatabase
import com.cas.musicplayer.data.enteties.durationToSeconds
import com.cas.musicplayer.player.EmplacementFullScreen
import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.player.VideoEmplacement
import com.cas.musicplayer.services.PlaybackDuration
import com.cas.musicplayer.services.PlaybackLiveData
import com.cas.musicplayer.ui.MainActivity
import com.cas.musicplayer.utils.*
import com.ncorti.slidetoact.SlideToActView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.fragment_bottom_panel.*
import java.util.concurrent.Executors


class BottomPanelFragment : Fragment(), SlidingUpPanelLayout.PanelSlideListener,
    SlideToActView.OnSlideCompleteListener, View.OnTouchListener {

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
        Log.d(TAG, "slideOffset = ${panel?.y}")
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

    val TAG = "BottomPanelFragment"

    lateinit var db: MusicTrackRoomDatabase

    var dialogBottomShet: PlayerBottomSheetFragment? = null

    lateinit var mainActivity: MainActivity

    private var visible = true


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bottom_panel, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mainActivity = requireActivity() as MainActivity

        mainActivity.slidingPaneLayout.addPanelSlideListener(this)


        db = MusicTrackRoomDatabase.getDatabase(context!!)

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
            Utils.shareVia(PlayerQueue.value?.shareVideoUrl)
        }

        btnAddFav.setOnClickListener {
            if (!UserPrefs.isFav(PlayerQueue.value?.youtubeId)) {
                Executors.newSingleThreadExecutor().execute {
                    db.musicTrackDao().insertMusicTrack(PlayerQueue.value!!)
                }
                UserPrefs.saveFav(PlayerQueue.value?.youtubeId, true)
                btnAddFav.setImageResource(R.drawable.ic_favorite_added_24dp)
            } else {
                Executors.newSingleThreadExecutor().execute {
                    db.musicTrackDao().deleteMusicTrack(PlayerQueue.value!!.youtubeId)
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
    }

    private fun showQueue() {
        dialogBottomShet = PlayerBottomSheetFragment()
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

        paramsTitle.topMargin = emplacementCenter.y - requireActivity().dpToPixel(40f)
        paramsTitle.marginStart = horizontalMargin
        paramsTitle.leftMargin = horizontalMargin
        paramsTitle.marginEnd = horizontalMargin
        paramsTitle.rightMargin = horizontalMargin

        txtTitleVideoCenter.layoutParams = paramsTitle

        val paramsTxtYoutubeCopy = btnYoutube.layoutParams as RelativeLayout.LayoutParams
        paramsTxtYoutubeCopy.topMargin = paramsTitle.topMargin - requireActivity().dpToPixel(40f)
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
                imgBlured.setImageBitmap(BlurImage.fastblur(bitmap, 1f, 45))
            }
        }

        imgBlured.tag = target
        Picasso.get().load(video.imgUrl).into(target)
    }

    private fun lockScreen(lock: Boolean) {

        if (lock) {
            mainActivity.slidingMenu.isMenuLocked = true

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

        } else {
            mainActivity.slidingMenu.isMenuLocked = false

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
        if (Build.VERSION.SDK_INT > 10) {
            var flags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_FULLSCREEN

            if (isImmersiveAvailable()) {
                flags =
                    flags or (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            }

            activity.window.decorView.systemUiVisibility = flags
        } else {
            activity.window
                .setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
                )
        }
    }

    private fun exitFullscreen(activity: Activity) {
        if (Build.VERSION.SDK_INT > 10) {
            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        } else {
            activity.window
                .setFlags(
                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN
                )
        }
    }

    private fun isImmersiveAvailable(): Boolean {
        return Build.VERSION.SDK_INT >= 19
    }
}