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
import com.cas.musicplayer.ui.common.songs.AppImage
import com.cas.musicplayer.ui.common.songs.BaseSongsFragment
import com.cas.musicplayer.ui.library.adapters.LibraryAdapter
import com.cas.musicplayer.ui.playlist.custom.CustomPlaylistSongsFragment
import com.cas.musicplayer.utils.dpToPixel
import com.cas.musicplayer.utils.navigateSafeAction
import kotlinx.android.synthetic.main.fragment_library.*

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-28.
 ***************************************
 */
class LibraryFragment : BaseFragment<LibraryViewModel>(
    R.layout.fragment_library
) {

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
        adjustStatusBarWithTheme()
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect, view: View,
                parent: RecyclerView, state: RecyclerView.State
            ) {
                val context = parent.context
                val position = parent.getChildAdapterPosition(view)
                with(outRect) {
                    if (position > 1) {
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
            findNavController().navigateSafeAction(R.id.action_libraryFragment_to_settingsFragment)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun observeViewModel() {
        observe(viewModel.recentSongs) {
            adapter.updateRecent(it)
        }
        observe(viewModel.heavySongs) {
            adapter.updateHeavy(it)
        }
        observe(viewModel.favouriteSongs) {
            adapter.updateFavourite(it)
        }
        observe(viewModel.playlists) {
            adapter.updatePlaylists(it)
        }
        observeEvent(viewModel.onClickSong) {
            (activity as? MainActivity)?.collapseBottomPanel()
        }
        observeEvent(viewModel.onClickPlaylist) { playList ->
            findNavController().navigate(
                R.id.action_libraryFragment_to_customPlaylistSongsFragment,
                bundleOf(
                    BaseSongsFragment.EXTRAS_ID_FEATURED_IMAGE to AppImage.AppImageUrl(
                        playList.urlImage
                    ),
                    CustomPlaylistSongsFragment.EXTRAS_PLAYLIST to playList
                )
            )
        }
    }
}