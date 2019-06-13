package com.secureappinc.musicplayer.ui.genres.detailgenre.playlists


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.secureappinc.musicplayer.R
import com.secureappinc.musicplayer.base.common.Status
import com.secureappinc.musicplayer.ui.BaseFragment
import com.secureappinc.musicplayer.ui.genres.detailgenre.DetailGenreFragment
import com.secureappinc.musicplayer.ui.home.models.GenreMusic
import com.secureappinc.musicplayer.utils.gone
import com.secureappinc.musicplayer.utils.visible
import kotlinx.android.synthetic.main.fragment_genre_videos.*
import javax.inject.Inject


class GenrePlaylistsFragment : BaseFragment() {

    val TAG = "DetailCategoryFragment"

    lateinit var viewModel: GenrePlaylistsViewModel

    @Inject
    lateinit var genrePlaylistsViewModelFactory: GenrePlaylistsViewModelFactory

    lateinit var adapter: GenrePlaylistsAdapter
    lateinit var genreMusic: GenreMusic

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_genre_videos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        app().appComponent.inject(this)
        val parcelableGenre = arguments?.getParcelable<GenreMusic>(DetailGenreFragment.EXTRAS_GENRE)
        if (parcelableGenre == null) {
            requireActivity().onBackPressed()
            return
        }

        genreMusic = parcelableGenre
        adapter = GenrePlaylistsAdapter(listOf(), genreMusic)
        recyclerView.adapter = adapter

        viewModel = ViewModelProviders.of(this, genrePlaylistsViewModelFactory).get(GenrePlaylistsViewModel::class.java)
        viewModel.loadTopTracks(genreMusic.channelId)

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.playlists.observe(this, Observer { resource ->
            when (resource.status) {
                Status.LOADING -> {

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
