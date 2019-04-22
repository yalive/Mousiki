package com.secureappinc.musicplayer.ui.bottompanel


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.RelativeLayout
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.secureappinc.musicplayer.models.VideoEmplacement
import com.secureappinc.musicplayer.models.enteties.MusicTrack
import com.secureappinc.musicplayer.models.enteties.MusicTrackRoomDatabase
import com.secureappinc.musicplayer.player.PlayerQueue
import com.secureappinc.musicplayer.services.PlaybackDuration
import com.secureappinc.musicplayer.services.PlaybackLiveData
import com.secureappinc.musicplayer.ui.MainActivity
import com.secureappinc.musicplayer.ui.fullscreen.FullscreenPlayerActivity
import com.secureappinc.musicplayer.utils.*
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.fragment_bottom_panel.*
import java.util.concurrent.Executors


class BottomPanelFragment : Fragment() {

    val TAG = "BottomPanelFragment"

    lateinit var db: MusicTrackRoomDatabase

    var dialogBottomShet: PlayerBottomSheetFragment? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.secureappinc.musicplayer.R.layout.fragment_bottom_panel, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val mainActivity = requireActivity() as MainActivity
        mainActivity.slidingPaneLayout.addPanelSlideListener(object : SlidingUpPanelLayout.PanelSlideListener {
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
        })


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
                btnAddFav.setImageResource(com.secureappinc.musicplayer.R.drawable.ic_favorite_added_24dp)
            } else {
                Executors.newSingleThreadExecutor().execute {
                    db.musicTrackDao().deleteMusicTrack(PlayerQueue.value!!.youtubeId)
                }
                UserPrefs.saveFav(PlayerQueue.value?.youtubeId, false)
                btnAddFav.setImageResource(com.secureappinc.musicplayer.R.drawable.ic_favorite_border)

            }

        }

        PlaybackLiveData.observe(this, Observer {
            if (it == PlayerConstants.PlayerState.PAUSED) {
                btnPlayPause.setImageResource(com.secureappinc.musicplayer.R.drawable.ic_play)
                btnPlayPauseMain.setImageResource(com.secureappinc.musicplayer.R.drawable.ic_play)
            } else if (it == PlayerConstants.PlayerState.PLAYING) {
                btnPlayPause.setImageResource(com.secureappinc.musicplayer.R.drawable.ic_pause)
                btnPlayPauseMain.setImageResource(com.secureappinc.musicplayer.R.drawable.ic_pause)
            }
        })

        btnClosePanel.setOnClickListener {
            mainActivity.showBottomPanel()
        }

        btnPlayNext.setOnClickListener {
            PlayerQueue.playNextTrack()
        }

        btnPlayPrevious.setOnClickListener {
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
            val intent = Intent(requireContext(), FullscreenPlayerActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                dialogBottomShet?.onGlobalLayoutEvent()
            }
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
        val emplacementCenter = VideoEmplacement.center()

        val paramsTitle = txtTitleVideoCenter.layoutParams as RelativeLayout.LayoutParams
        val horizontalMargin = emplacementCenter.x

        paramsTitle.topMargin = emplacementCenter.y - requireActivity().dpToPixel(16f)
        paramsTitle.marginStart = horizontalMargin
        paramsTitle.leftMargin = horizontalMargin
        paramsTitle.marginEnd = horizontalMargin
        paramsTitle.rightMargin = horizontalMargin

        txtTitleVideoCenter.layoutParams = paramsTitle

        val paramsTxtYoutubeCopy = btnYoutube.layoutParams as RelativeLayout.LayoutParams
        paramsTxtYoutubeCopy.topMargin = paramsTitle.topMargin + requireActivity().dpToPixel(8f)
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
            btnAddFav.setImageResource(com.secureappinc.musicplayer.R.drawable.ic_favorite_added_24dp)
        } else {
            btnAddFav.setImageResource(com.secureappinc.musicplayer.R.drawable.ic_favorite_border)

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
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
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
            }

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
            }

            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                imgBlured.setImageBitmap(BlurImage.fastblur(bitmap, 1f, 45))
            }
        }

        imgBlured.tag = target
        Picasso.get().load(video.imgUrl).into(target)
    }
}
