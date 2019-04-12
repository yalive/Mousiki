package com.secureappinc.musicplayer.ui.player


import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.loadOrCueVideo
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.secureappinc.musicplayer.R
import kotlinx.android.synthetic.main.fragment_video_player.*


class VideoPlayerFragment : Fragment() {

    companion object {

        val EXTRAS_VIDEO_ID = "video-id"
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_video_player, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycle.addObserver(youtubePlayerView)

        val videoId = arguments?.getString(EXTRAS_VIDEO_ID)

        initPictureInPicture(youtubePlayerView)
        youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {

            override fun onReady(youTubePlayer: YouTubePlayer) {
                videoId?.let {
                    youTubePlayer.loadOrCueVideo(lifecycle, videoId, 0f)
                }
            }
        })
    }

    private fun initPictureInPicture(youTubePlayerView: YouTubePlayerView) {
        val pictureInPictureIcon = ImageView(requireContext())
        pictureInPictureIcon.setImageDrawable(
                ContextCompat.getDrawable(
                        requireContext(),
                        com.secureappinc.musicplayer.R.drawable.ic_picture_in_picture
                )
        )

        pictureInPictureIcon.setOnClickListener { view ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val supportsPIP =
                        requireContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
                if (supportsPIP) {
                    requireActivity().enterPictureInPictureMode()
                }
            } else {
                AlertDialog.Builder(requireContext())
                        .setTitle("Can't enter picture in picture mode")
                        .setMessage("In order to enter picture in picture mode you need a SDK version >= N.")
                        .show()
            }
        }

        youTubePlayerView.getPlayerUiController().addView(pictureInPictureIcon)
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode)

        if (isInPictureInPictureMode) {
            youtubePlayerView.enterFullScreen();
            youtubePlayerView.getPlayerUiController().showUi(false);
        } else {
            youtubePlayerView.exitFullScreen();
            youtubePlayerView.getPlayerUiController().showUi(true);
        }
    }
}
