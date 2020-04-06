package com.cas.musicplayer.ui.library

import android.graphics.Rect
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.extensions.observe
import com.cas.common.extensions.observeEvent
import com.cas.common.fragment.BaseFragment
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.di.injector.injector
import com.cas.musicplayer.ui.MainActivity
import com.cas.musicplayer.ui.common.songs.BaseSongsFragment
import com.cas.musicplayer.ui.common.songs.FeaturedImage
import com.cas.musicplayer.ui.library.adapters.LibraryAdapter
import com.cas.musicplayer.ui.playlist.custom.CustomPlaylistSongsFragment
import com.cas.musicplayer.utils.dpToPixel
import kotlinx.android.synthetic.main.fragment_library.*

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-28.
 ***************************************
 */
class LibraryFragment : BaseFragment<LibraryViewModel>() {

    override val layoutResourceId: Int = R.layout.fragment_library
    override val viewModel by viewModel { injector.libraryViewModel }
    override val screenTitle by lazy {
        getString(R.string.library)
    }
    private val adapter by lazy {
        LibraryAdapter(viewModel)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        darkStatusBarOnDarkMode()
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect, view: View,
                parent: RecyclerView, state: RecyclerView.State
            ) {
                val context = parent.context
                val position = parent.getChildAdapterPosition(view)
                with(outRect) {
                    if (position == 2) {
                        top = context.dpToPixel(24f)
                    } else if (position == 4) {
                        top = context.dpToPixel(24f)
                    }
                }
            }
        })
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadCustomPlaylists()
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
        observe(viewModel.playlists, adapter::updatePlaylists)
        observeEvent(viewModel.onClickSong) {
            (activity as? MainActivity)?.collapseBottomPanel()
        }
        observeEvent(viewModel.onClickPlaylist) { playList ->
            findNavController().navigate(
                R.id.action_libraryFragment_to_customPlaylistSongsFragment,
                bundleOf(
                    BaseSongsFragment.EXTRAS_ID_FEATURED_IMAGE to FeaturedImage.FeaturedImageUrl(
                        playList.urlImage
                    ),
                    CustomPlaylistSongsFragment.EXTRAS_PLAYLIST to playList
                )
            )
        }
    }
}