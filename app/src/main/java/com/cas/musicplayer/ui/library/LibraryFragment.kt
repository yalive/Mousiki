package com.cas.musicplayer.ui.library

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.updatePadding
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
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
    override val screenTitle by lazy {
        getString(R.string.library)
    }
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lightStatusBar()
        recyclerView.adapter = adapter
        observeViewModel()
        DeviceInset.observe(this, Observer { inset ->
            recyclerView.updatePadding(top = inset.top)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_home, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menuSetting) {
            findNavController().navigate(R.id.action_libraryFragment_to_settingsFragment)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun observeViewModel() {
        observe(viewModel.recentSongs, adapter::updateRecent)
        observe(viewModel.heavySongs, adapter::updateHeavy)
        observe(viewModel.favouriteSongs, adapter::updateFavourite)
    }

    override fun getPageTitle(): String = "Library"
}