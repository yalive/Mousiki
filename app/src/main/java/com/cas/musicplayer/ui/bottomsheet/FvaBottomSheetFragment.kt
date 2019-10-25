package com.cas.musicplayer.ui.bottomsheet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.cas.musicplayer.R
import com.cas.musicplayer.data.enteties.MusicTrack
import com.cas.musicplayer.data.enteties.MusicTrackRoomDatabase
import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.services.PlaybackLiveData
import com.cas.musicplayer.ui.MainActivity
import com.cas.musicplayer.utils.Extensions.injector
import com.cas.musicplayer.utils.UserPrefs
import com.cas.musicplayer.utils.Utils
import kotlinx.android.synthetic.main.fragment_bottom_sheet_dialog.*
import java.util.concurrent.Executors

/**
 * Created by Fayssel Yabahddou on 4/13/19.
 */
class FvaBottomSheetFragment : BottomSheetDialogFragment() {

    val TAG = "BottomSheetFragment"
    lateinit var musicTrack: MusicTrack

    lateinit var db: MusicTrackRoomDatabase

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bottom_sheet_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = MusicTrackRoomDatabase.getDatabase(context!!)

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
            Log.d(TAG, musicTrack.shareVideoUrl)
            Utils.shareVia(musicTrack.shareVideoUrl)
            if (this.isVisible) {
                this.dismiss()
            }
        }

        favController.setOnClickListener {
            Executors.newSingleThreadExecutor().execute {
                if (UserPrefs.isFav(musicTrack.youtubeId)) {
                    db.musicTrackDao().deleteMusicTrack(musicTrack.youtubeId)
                    UserPrefs.saveFav(musicTrack.youtubeId, false)
                } else {
                    db.musicTrackDao().insertMusicTrack(musicTrack)
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