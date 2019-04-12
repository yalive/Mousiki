package com.secureappinc.musicplayer.ui.bottompanel


import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.secureappinc.musicplayer.models.SnippetVideo
import com.secureappinc.musicplayer.models.VideoEmplacement
import com.secureappinc.musicplayer.services.PlaybackLiveData
import com.secureappinc.musicplayer.services.VideoPlayBackState
import com.secureappinc.musicplayer.ui.MainActivity
import com.secureappinc.musicplayer.ui.MainViewModel
import com.secureappinc.musicplayer.utils.BlurImage
import com.secureappinc.musicplayer.utils.dpToPixel
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.fragment_bottom_panel.*


class BottomPanelFragment : Fragment() {

    val TAG = "BottomPanelFragment"

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

            }
        })


        val viewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java)
        viewModel.currentVideo.observe(this, Observer { video ->
            onVideoChanged(video)
        })


        btnPlayPause.setOnClickListener {
            onClickPlayPause()
        }

        btnPlayPauseMain.setOnClickListener {
            onClickPlayPause()
        }

        PlaybackLiveData.observe(this, Observer {
            if (it.state == PlayerConstants.PlayerState.PAUSED) {
                btnPlayPause.setImageResource(com.secureappinc.musicplayer.R.drawable.ic_play)
                btnPlayPauseMain.setImageResource(com.secureappinc.musicplayer.R.drawable.ic_play)
            } else if (it.state == PlayerConstants.PlayerState.PLAYING) {
                btnPlayPause.setImageResource(com.secureappinc.musicplayer.R.drawable.ic_pause)
                btnPlayPauseMain.setImageResource(com.secureappinc.musicplayer.R.drawable.ic_pause)
            }
        })

        btnClosePanel.setOnClickListener {
            mainActivity.slidingPaneLayout.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
        }

        adjustCenterViews()
    }

    private fun onClickPlayPause() {
        val oldState = PlaybackLiveData.value
        oldState?.let { playerState ->

            if (playerState.state == PlayerConstants.PlayerState.PLAYING) {
                PlaybackLiveData.value = VideoPlayBackState(PlayerConstants.PlayerState.PAUSED, true)
            } else if ((playerState.state == PlayerConstants.PlayerState.PAUSED)) {
                PlaybackLiveData.value = VideoPlayBackState(PlayerConstants.PlayerState.PLAYING, true)
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

    private fun onVideoChanged(video: SnippetVideo) {

        txtTitle.text = video.title
        txtTitleVideoCenter.text = video.title

        val mainActivity = requireActivity() as MainActivity
        mainActivity.slidingPaneLayout.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED

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
        Picasso.get().load(video.thumbnails.high.url).into(target)
    }
}
