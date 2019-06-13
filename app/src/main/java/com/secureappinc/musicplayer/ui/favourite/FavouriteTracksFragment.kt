package com.secureappinc.musicplayer.ui.favourite


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.secureappinc.musicplayer.R
import com.secureappinc.musicplayer.data.enteties.MusicTrack
import com.secureappinc.musicplayer.data.enteties.MusicTrackRoomDatabase
import com.secureappinc.musicplayer.ui.MainActivity
import com.secureappinc.musicplayer.ui.bottomsheet.FvaBottomSheetFragment
import com.secureappinc.musicplayer.utils.gone
import com.secureappinc.musicplayer.utils.visible
import kotlinx.android.synthetic.main.fragment_play_list.*

class FavouriteTracksFragment : Fragment(), FavouriteTracksAdapter.onItemClickListener {

    val TAG = "PlayListFragment"

    lateinit var db: MusicTrackRoomDatabase

    lateinit var adapter: FavouriteTracksAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_play_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = MusicTrackRoomDatabase.getDatabase(context!!)

        adapter = FavouriteTracksAdapter(listOf(), this)

        recyclerView.adapter = adapter

        db.musicTrackDao().getAllMusicTrack().observe(this, Observer {

            if (!it.isEmpty()) {
                recyclerView.visible()
                imgNoSongs.gone()
                txtError.gone()
                adapter.items = it
            } else {
                recyclerView.gone()
                imgNoSongs.visible()
                txtError.visible()
            }

        })

    }

    override fun onItemClick(musicTrack: MusicTrack) {
        val bottomSheetFragment = FvaBottomSheetFragment()
        val bundle = Bundle()
        bundle.putString("MUSIC_TRACK", Gson().toJson(musicTrack))
        bottomSheetFragment.arguments = bundle
        bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
    }

    override fun onSelectVideo(musicTrack: MusicTrack) {
        val mainActivity = requireActivity() as MainActivity
        mainActivity.collapseBottomPanel()
    }
}
