package com.cas.musicplayer.ui.genres.detailgenre.videos


import android.os.Bundle
import android.view.View
import com.cas.common.adapter.PageableFragment
import com.cas.common.extensions.gone
import com.cas.common.extensions.observe
import com.cas.common.extensions.visible
import com.cas.common.fragment.BaseFragment
import com.cas.common.resource.Resource
import com.cas.common.viewmodel.activityViewModel
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.di.injector.injector
import com.cas.musicplayer.domain.model.GenreMusic
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.ui.MainActivity
import com.cas.musicplayer.ui.common.SongsAdapter
import com.cas.musicplayer.ui.bottomsheet.FvaBottomSheetFragment
import com.cas.musicplayer.ui.genres.detailgenre.DetailGenreFragment
import com.cas.musicplayer.ui.home.model.DisplayedVideoItem
import kotlinx.android.synthetic.main.fragment_genre_videos.*


class GenreVideosFragment : BaseFragment<GenreVideosViewModel>(), PageableFragment {
    override val layoutResourceId: Int = R.layout.fragment_genre_videos
    override val viewModel by viewModel { injector.genreVideosViewModel }

    private val adapter by lazy {
        SongsAdapter(
            onVideoSelected = { track ->
                val mainActivity = requireActivity() as MainActivity
                mainActivity.collapseBottomPanel()
                viewModel.onClickTrack(track)
            },
            onClickMore = { track ->
                showBottomMenuButtons(track)
            }
        )
    }

    private lateinit var genreMusic: GenreMusic
    private val detailGenreViewModel by activityViewModel { injector.detailGenreViewModel }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val parcelableGenre = arguments?.getParcelable<GenreMusic>(DetailGenreFragment.EXTRAS_GENRE)
        if (parcelableGenre == null) {
            requireActivity().onBackPressed()
            return
        }
        genreMusic = parcelableGenre
        recyclerView.adapter = adapter
        viewModel.loadTopTracks(genreMusic.topTracksPlaylist)
        observe(viewModel.tracks, this::updateUI)
    }

    override fun getPageTitle(): String = "Videos"

    private fun updateUI(resource: Resource<List<DisplayedVideoItem>>) = when (resource) {
        is Resource.Success -> {
            val videos = resource.data
            adapter.dataItems = videos.toMutableList()
            recyclerView.visible()
            progressBar.gone()
            txtError.gone()
            detailGenreViewModel.firstTrack.value = videos[0].track
        }
        is Resource.Failure -> {
            progressBar.gone()
            txtError.visible()
        }
        is Resource.Loading -> {
            progressBar.visible()
            txtError.gone()
        }
    }

    private fun showBottomMenuButtons(musicTrack: MusicTrack) {
        val bottomSheetFragment = FvaBottomSheetFragment()
        val bundle = Bundle()
        bundle.putString("MUSIC_TRACK", injector.gson.toJson(musicTrack))
        bottomSheetFragment.arguments = bundle
        bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
    }
}
