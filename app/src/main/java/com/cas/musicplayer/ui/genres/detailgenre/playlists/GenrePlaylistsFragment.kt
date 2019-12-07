package com.cas.musicplayer.ui.genres.detailgenre.playlists


import android.os.Bundle
import android.view.View
import com.cas.common.adapter.PageableFragment
import com.cas.common.extensions.gone
import com.cas.common.extensions.observe
import com.cas.common.extensions.visible
import com.cas.common.fragment.BaseFragment
import com.cas.common.resource.Resource
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.di.injector.injector
import com.cas.musicplayer.domain.model.GenreMusic
import com.cas.musicplayer.domain.model.Playlist
import com.cas.musicplayer.ui.common.playlist.PlaylistsAdapter
import com.cas.musicplayer.ui.genres.detailgenre.DetailGenreFragment
import kotlinx.android.synthetic.main.fragment_genre_videos.*


class GenrePlaylistsFragment : BaseFragment<GenrePlaylistsViewModel>(), PageableFragment {

    override val layoutResourceId: Int = R.layout.fragment_genre_videos
    override val viewModel by viewModel { injector.genrePlaylistsViewModel }

    private lateinit var genreMusic: GenreMusic

    private val adapter by lazy { PlaylistsAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val parcelableGenre = arguments?.getParcelable<GenreMusic>(DetailGenreFragment.EXTRAS_GENRE)
        if (parcelableGenre == null) {
            requireActivity().onBackPressed()
            return
        }
        genreMusic = parcelableGenre
        recyclerView.adapter = adapter
        viewModel.loadTopTracks(genreMusic.channelId)
        observeViewModel()
    }

    override fun getPageTitle(): String = "Playlist"

    private fun observeViewModel() {
        observe(viewModel.playlists, this::updateList)
    }

    private fun updateList(resource: Resource<List<Playlist>>) {
        when (resource) {
            is Resource.Loading -> {
                // Review
            }
            is Resource.Failure -> {
                showError()
            }
            is Resource.Success -> {
                showSuccess()
                adapter.dataItems = resource.data.toMutableList()
            }
        }
    }

    private fun showSuccess() {
        progressBar.gone()
        recyclerView.visible()
        txtError.gone()
    }

    private fun showError() {
        progressBar.gone()
        recyclerView.gone()
        txtError.visible()
    }
}
