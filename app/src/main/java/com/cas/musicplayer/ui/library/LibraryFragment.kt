package com.cas.musicplayer.ui.library

import android.os.Bundle
import android.view.View
import androidx.core.view.updatePadding
import androidx.lifecycle.Observer
import com.cas.common.adapter.PageableFragment
import com.cas.common.extensions.observe
import com.cas.common.fragment.BaseFragment
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.di.injector.injector
import com.cas.musicplayer.ui.MainActivity
import com.cas.musicplayer.ui.library.adapters.LibraryAdapter
import com.cas.musicplayer.utils.DeviceInset
import kotlinx.android.synthetic.main.fragment_library.*

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-28.
 ***************************************
 */
class LibraryFragment : BaseFragment<LibraryViewModel>(), PageableFragment {

    override val layoutResourceId: Int = R.layout.fragment_library
    override val viewModel by viewModel { injector.libraryViewModel }

    private val adapter = LibraryAdapter(
        onRecentSongSelected = {
            (activity as? MainActivity)?.collapseBottomPanel()
            viewModel.onClickRecentTrack(it)
        },
        onHeavySongSelected = {
            (activity as? MainActivity)?.collapseBottomPanel()
            viewModel.onClickHeavyTrack(it)
        },
        onFavouriteSongSelected = {
            (activity as? MainActivity)?.collapseBottomPanel()
            viewModel.onClickFavouriteTrack(it)
        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lightStatusBar()
        recyclerView.adapter = adapter
        observeViewModel()
        DeviceInset.observe(this, Observer { inset ->
            recyclerView.updatePadding(top = inset.top)
        })
    }

    private fun observeViewModel() {
        observe(viewModel.recentSongs, adapter::updateRecent)
        observe(viewModel.heavySongs, adapter::updateHeavy)
        observe(viewModel.favouriteSongs, adapter::updateFavourite)
    }

    override fun getPageTitle(): String = "Library"
}