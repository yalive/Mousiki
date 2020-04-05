package com.cas.musicplayer.ui.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.cas.common.extensions.onClick
import com.cas.musicplayer.R
import com.cas.musicplayer.di.injector.injector
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.player.services.PlaybackLiveData
import com.cas.musicplayer.ui.MainActivity
import com.cas.musicplayer.ui.playlist.create.AddTrackToPlaylistFragment
import com.cas.musicplayer.utils.UserPrefs
import com.cas.musicplayer.utils.Utils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import kotlinx.android.synthetic.main.fragment_bottom_sheet_dialog.*
import java.util.concurrent.Executors

/**
 * Created by Fayssel Yabahddou on 4/13/19.
 */
class FvaBottomSheetFragment : BottomSheetDialogFragment() {

    lateinit var musicTrack: MusicTrack

    private val viewModel by lazy { injector.favBottomSheetViewModel }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bottom_sheet_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val json = arguments?.getString("MUSIC_TRACK")
        musicTrack = injector.gson.fromJson(json, MusicTrack::class.java)

        if (!UserPrefs.isFav(musicTrack.youtubeId)) {
            favIcon.setImageResource(R.drawable.ic_favorite_border_yellow)
            favLabel.text = "Favorite"
        } else {
            favIcon.setImageResource(R.drawable.ic_favorite_added_yellow)
            favLabel.text = "Unfavorite"
        }

        shareVia.setOnClickListener {
            Utils.shareWithDeepLink(musicTrack, context!!)
            if (this.isVisible) {
                this.dismiss()
            }
        }

        favController.setOnClickListener {
            Executors.newSingleThreadExecutor().execute {
                if (UserPrefs.isFav(musicTrack.youtubeId)) {
                    viewModel.removeSongFromFavourite(musicTrack)
                    UserPrefs.saveFav(musicTrack.youtubeId, false)
                } else {
                    viewModel.makeSongAsFavourite(musicTrack)
                    UserPrefs.saveFav(musicTrack.youtubeId, true)
                }
            }

            if (this.isVisible) {
                this.dismiss()
            }
        }

        btnAddAsNext.setOnClickListener {
            PlayerQueue.addAsNext(musicTrack)
            this.dismiss()
        }

        btnAddToPlaylist.onClick {
            val navOptions = navOptions {
                anim {
                    enter = R.anim.fad_in
                    exit = R.anim.fad_out
                }
            }
            findNavController().navigate(
                R.id.addTrackToPlaylistFragment, bundleOf(
                    AddTrackToPlaylistFragment.EXTRAS_TRACK to musicTrack,
                    AddTrackToPlaylistFragment.EXTRAS_CURRENT_DESTINATION to findNavController().currentDestination?.id
                ), navOptions
            )
            dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        if (PlaybackLiveData.value != null && PlaybackLiveData.value != PlayerConstants.PlayerState.UNKNOWN) {
            PlayerQueue.hideVideo()
        }
    }

    override fun onPause() {
        super.onPause()
        val mainActivity = requireActivity() as MainActivity
        if (mainActivity.isBottomPanelCollapsed() && PlaybackLiveData.value != null && PlaybackLiveData.value != PlayerConstants.PlayerState.UNKNOWN) {
            PlayerQueue.showVideo()
            PlayerQueue.resume()
        }
    }
}