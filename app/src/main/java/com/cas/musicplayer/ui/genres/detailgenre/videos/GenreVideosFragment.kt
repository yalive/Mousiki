package com.cas.musicplayer.ui.genres.detailgenre.videos


import android.os.Bundle
import android.view.View
import com.cas.musicplayer.R
import com.cas.musicplayer.base.common.PageableFragment
import com.cas.musicplayer.base.common.Resource
import com.cas.musicplayer.data.enteties.MusicTrack
import com.cas.musicplayer.ui.BaseFragment
import com.cas.musicplayer.ui.MainActivity
import com.cas.musicplayer.ui.bottomsheet.FvaBottomSheetFragment
import com.cas.musicplayer.ui.genres.detailgenre.DetailGenreFragment
import com.cas.musicplayer.ui.home.domain.model.GenreMusic
import com.cas.musicplayer.ui.home.ui.model.DisplayedVideoItem
import com.cas.musicplayer.utils.Extensions.injector
import com.cas.musicplayer.utils.gone
import com.cas.musicplayer.utils.observe
import com.cas.musicplayer.utils.visible
import com.cas.musicplayer.viewmodel.activityViewModel
import com.cas.musicplayer.viewmodel.viewModel
import kotlinx.android.synthetic.main.fragment_genre_videos.*


class GenreVideosFragment : BaseFragment<GenreVideosViewModel>(), PageableFragment {
    override val layoutResourceId: Int = R.layout.fragment_genre_videos
    override val viewModel by viewModel { injector.genreVideosViewModel }

    private lateinit var adapter: GenreVideosAdapter
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
        adapter = GenreVideosAdapter(
            genreMusic = genreMusic,
            onVideoSelected = {
                val mainActivity = requireActivity() as MainActivity
                mainActivity.collapseBottomPanel()
            },
            onClickMore = { track ->
                showBottomMenuButtons(track)
            }
        )
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
