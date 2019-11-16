package com.cas.musicplayer.ui.genres.detailgenre.playlists


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.cas.musicplayer.R
import com.cas.musicplayer.base.common.Status
import com.cas.musicplayer.ui.BaseFragment
import com.cas.musicplayer.ui.genres.detailgenre.DetailGenreFragment
import com.cas.musicplayer.ui.home.domain.model.GenreMusic
import com.cas.musicplayer.utils.Extensions.injector
import com.cas.musicplayer.utils.gone
import com.cas.musicplayer.utils.visible
import com.cas.musicplayer.viewmodel.viewModel
import kotlinx.android.synthetic.main.fragment_genre_videos.*


class GenrePlaylistsFragment : Fragment() {

    val TAG = "DetailCategoryFragment"


    private val viewModel by viewModel { injector.genrePlaylistsViewModel }

    lateinit var adapter: GenrePlaylistsAdapter
    lateinit var genreMusic: GenreMusic

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_genre_videos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val parcelableGenre = arguments?.getParcelable<GenreMusic>(DetailGenreFragment.EXTRAS_GENRE)
        if (parcelableGenre == null) {
            requireActivity().onBackPressed()
            return
        }

        genreMusic = parcelableGenre
        adapter = GenrePlaylistsAdapter(listOf(), genreMusic)
        recyclerView.adapter = adapter

        viewModel.loadTopTracks(genreMusic.channelId)

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.playlists.observe(this, Observer { resource ->
            when (resource.status) {
                Status.LOADING -> {
                    // Review
                }
                Status.ERROR -> {
                    showError()
                }
                Status.SUCCESS -> {
                    showSuccess()
                    adapter.items = resource.data!!.filter { it.itemCount > 0 }
                }
            }
        })
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
