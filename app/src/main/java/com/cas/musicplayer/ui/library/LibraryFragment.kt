package com.cas.musicplayer.ui.library

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.cas.common.adapter.PageableFragment
import com.cas.common.extensions.gone
import com.cas.common.extensions.observe
import com.cas.common.extensions.visible
import com.cas.common.fragment.BaseFragment
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.data.local.database.MusicTrackRoomDatabase
import com.cas.musicplayer.di.injector.injector
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.ui.MainActivity
import com.cas.musicplayer.ui.bottomsheet.FvaBottomSheetFragment
import com.cas.musicplayer.ui.favourite.FavouriteTracksAdapter
import com.cas.musicplayer.ui.home.adapters.HomePopularSongsAdapter
import com.cas.musicplayer.ui.home.adapters.HomeRecentPlayedSongsAdapter
import com.cas.musicplayer.ui.home.adapters.RecentPlayedSongItem
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_library.*

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-28.
 ***************************************
 */
class LibraryFragment : BaseFragment<LibraryViewModel>(), PageableFragment, FavouriteTracksAdapter.OnItemClickListener {

    override val layoutResourceId: Int = R.layout.fragment_library
    override val viewModel by viewModel { injector.libraryViewModel }

    private lateinit var db: MusicTrackRoomDatabase

    private lateinit var favouriteAdapter: FavouriteTracksAdapter
    private val recentAdapter = HomeRecentPlayedSongsAdapter({})
    private val heavyAdapter = HomePopularSongsAdapter({})

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerViewRecent.adapter = recentAdapter
        recyclerViewHeavy.adapter = heavyAdapter
        // favourites
        db = MusicTrackRoomDatabase.getDatabase(context!!)
        favouriteAdapter = FavouriteTracksAdapter(listOf(), this)
        recyclerViewFavourite.adapter = favouriteAdapter
        db.musicTrackDao().getAllMusicTrack().observe(this, Observer {
            if (it.isNotEmpty()) {
                recyclerViewFavourite.visible()
                favouriteAdapter.items = it
            } else {
                recyclerViewFavourite.gone()
            }
        })
        observeViewModel()
    }

    private fun observeViewModel() {
        observe(viewModel.recentSongs) { recentTracks ->
            val list: List<RecentPlayedSongItem> = recentTracks.map {
                RecentPlayedSongItem.RecentSong(it)
            }
            recentAdapter.dataItems = list.toMutableList()
        }

        observe(viewModel.heavySongs) { heavyTracks ->
            heavyAdapter.dataItems = heavyTracks.toMutableList()
        }
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

    override fun getPageTitle(): String = "Library"
}