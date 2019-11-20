package com.cas.musicplayer.ui.genres.detailgenre.playlists


import android.os.Bundle
import android.view.View
import com.cas.musicplayer.R
import com.cas.musicplayer.base.common.PageableFragment
import com.cas.musicplayer.base.common.Resource
import com.cas.musicplayer.data.enteties.Playlist
import com.cas.musicplayer.ui.BaseFragment
import com.cas.musicplayer.ui.genres.detailgenre.DetailGenreFragment
import com.cas.musicplayer.ui.home.domain.model.GenreMusic
import com.cas.musicplayer.utils.Extensions.injector
import com.cas.musicplayer.utils.gone
import com.cas.musicplayer.utils.observe
import com.cas.musicplayer.utils.visible
import com.cas.musicplayer.viewmodel.viewModel
import kotlinx.android.synthetic.main.fragment_genre_videos.*


class GenrePlaylistsFragment : BaseFragment<GenrePlaylistsViewModel>(), PageableFragment {

    override val layoutResourceId: Int = R.layout.fragment_genre_videos
    override val viewModel by viewModel { injector.genrePlaylistsViewModel }

    private lateinit var adapter: GenrePlaylistsAdapter
    private lateinit var genreMusic: GenreMusic

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val parcelableGenre = arguments?.getParcelable<GenreMusic>(DetailGenreFragment.EXTRAS_GENRE)
        if (parcelableGenre == null) {
            requireActivity().onBackPressed()
            return
        }
        genreMusic = parcelableGenre
        adapter = GenrePlaylistsAdapter(genreMusic)
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
