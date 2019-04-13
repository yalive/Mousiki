package com.secureappinc.musicplayer.ui.bottomsheet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.secureappinc.musicplayer.R
import com.secureappinc.musicplayer.models.enteties.MusicTrack
import com.secureappinc.musicplayer.models.enteties.MusicTrackRoomDatabase
import com.secureappinc.musicplayer.player.PlayerQueue
import com.secureappinc.musicplayer.utils.UserPrefs
import com.secureappinc.musicplayer.utils.Utils
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

        PlayerQueue.hideVideo()

        db = MusicTrackRoomDatabase.getDatabase(context!!)

        val json = arguments?.getString("MUSIC_TRACK")
        musicTrack = Gson().fromJson(json, MusicTrack::class.java)

        shareVia.setOnClickListener {
            Log.d(TAG, musicTrack.shareVideoUrl)
            Utils.shareVia(this, musicTrack.shareVideoUrl)
            if (this.isVisible) {
                this.dismiss()
            }
        }

        removeFavorite.setOnClickListener {
            Executors.newSingleThreadExecutor().execute {
                db.musicTrackDao().deleteMusicTrack(musicTrack.youtubeId)
            }
            UserPrefs.saveFav(musicTrack.youtubeId, false)

            if (this.isVisible) {
                this.dismiss()
            }
        }

        btnAddAsNext.setOnClickListener {
            PlayerQueue.addAsNext(musicTrack)
            this.dismiss()
        }
    }

    override fun onPause() {
        super.onPause()
        PlayerQueue.showVideo()
    }
}