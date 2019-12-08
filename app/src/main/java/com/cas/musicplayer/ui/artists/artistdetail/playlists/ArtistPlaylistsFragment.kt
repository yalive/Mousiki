package com.cas.musicplayer.ui.artists.artistdetail.playlists


import android.os.Bundle
import android.view.View
import com.cas.musicplayer.R
import com.cas.common.adapter.PageableFragment
import com.cas.common.resource.Resource
import com.cas.musicplayer.domain.model.Playlist
import com.cas.musicplayer.data.remote.models.Artist
import com.cas.common.fragment.BaseFragment
import com.cas.musicplayer.ui.artists.artistdetail.ArtistFragment
import com.cas.musicplayer.di.injector.injector
import com.cas.common.extensions.gone
import com.cas.common.extensions.observe
import com.cas.common.extensions.visible
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.ui.common.playlist.PlaylistsAdapter
import kotlinx.android.synthetic.main.fragment_artist_playlists.*


class ArtistPlaylistsFragment : BaseFragment<ArtistPlaylistsViewModel>(), PageableFragment {

    override val layoutResourceId: Int = R.layout.fragment_artist_playlists
    override val viewModel by viewModel { injector.artistPlaylistsViewModel }

    private val artist: Artist by lazy {
        val parcelableArtist = arguments?.getParcelable<Artist>(ArtistFragment.EXTRAS_ARTIST)
        parcelableArtist!!
    }

    private val adapter by lazy { PlaylistsAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.adapter = adapter
        loadPlaylist()
    }

    override fun getPageTitle(): String = "Playlist"

    private fun loadPlaylist() {
        viewModel.loadPlaylists(artist.channelId)
        observe(viewModel.playlists, this::updateUI)
    }

    private fun updateUI(resource: Resource<List<Playlist>>) {
        when (resource) {
            is Resource.Success -> showSuccess(resource.data)
            is Resource.Failure -> showError()
        }
    }

    private fun showSuccess(playlists: List<Playlist>) {
        progressBar?.gone()
        recyclerView?.visible()
        txtError?.gone()
        adapter.dataItems = playlists.toMutableList()
    }

    private fun showError() {
        progressBar?.gone()
        recyclerView?.gone()
        txtError?.visible()
    }
}
