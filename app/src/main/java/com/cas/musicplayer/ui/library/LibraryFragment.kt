package com.cas.musicplayer.ui.library

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.cas.common.adapter.PageableFragment
import com.cas.common.extensions.observe
import com.cas.common.fragment.BaseFragment
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.data.local.database.MusicTrackRoomDatabase
import com.cas.musicplayer.di.injector.injector
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.ui.MainActivity
import com.cas.musicplayer.ui.bottomsheet.FvaBottomSheetFragment
import com.cas.musicplayer.ui.favourite.FavouriteTracksAdapter
import com.cas.musicplayer.ui.home.model.toDisplayedVideoItem
import com.cas.musicplayer.ui.library.adapters.LibraryAdapter
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
    private val adapter = LibraryAdapter { track ->
        (activity as? MainActivity)?.collapseBottomPanel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.adapter = adapter
        db = MusicTrackRoomDatabase.getDatabase(requireContext())
        db.musicTrackDao().getAllMusicTrack().observe(this, Observer {
            if (it.isNotEmpty()) {
                adapter.updateFavourite(it.map { musicTrack ->
                    musicTrack.toDisplayedVideoItem()
                })
            }
        })
        observeViewModel()
    }

    private fun observeViewModel() {
        observe(viewModel.recentSongs, adapter::updateRecent)
        observe(viewModel.heavySongs, adapter::updateHeavy)
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