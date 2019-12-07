package com.cas.musicplayer.ui.artists.artistdetail.videos


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
import com.cas.musicplayer.data.remote.models.Artist
import com.cas.musicplayer.di.injector.injector
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.ui.MainActivity
import com.cas.musicplayer.ui.artists.artistdetail.ArtistFragment
import com.cas.musicplayer.ui.bottomsheet.FvaBottomSheetFragment
import com.cas.musicplayer.ui.common.SongsAdapter
import com.cas.musicplayer.ui.home.model.DisplayedVideoItem
import kotlinx.android.synthetic.main.fragment_genre_videos.*


class ArtistVideosFragment : BaseFragment<ArtistVideosViewModel>(), PageableFragment {

    override val viewModel by viewModel { injector.artistVideosViewModel }
    override val layoutResourceId: Int = R.layout.fragment_artist_videos
    private val adapter: SongsAdapter  by lazy {
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

    private val artist: Artist by lazy {
        arguments?.getParcelable<Artist>(ArtistFragment.EXTRAS_ARTIST)!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.adapter = adapter
        observe(viewModel.tracks, this::updateUI)
        viewModel.loadArtistTracks(artist.channelId)
        requireActivity().title = artist.name
    }

    override fun getPageTitle(): String = "Videos"

    private fun updateUI(resource: Resource<List<DisplayedVideoItem>>) = when (resource) {
        is Resource.Success -> {
            adapter.dataItems = resource.data.toMutableList()
            recyclerView.visible()
            progressBar.gone()
            txtError.gone()
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
