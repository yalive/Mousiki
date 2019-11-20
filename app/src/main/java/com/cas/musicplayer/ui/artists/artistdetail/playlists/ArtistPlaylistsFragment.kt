package com.cas.musicplayer.ui.artists.artistdetail.playlists


import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.cas.musicplayer.R
import com.cas.musicplayer.base.common.PageableFragment
import com.cas.musicplayer.base.common.Resource
import com.cas.musicplayer.data.enteties.Playlist
import com.cas.musicplayer.data.models.Artist
import com.cas.musicplayer.ui.BaseFragment
import com.cas.musicplayer.ui.artists.artistdetail.ArtistFragment
import com.cas.musicplayer.ui.playlistvideos.PlaylistVideosFragment
import com.cas.musicplayer.utils.Extensions.injector
import com.cas.musicplayer.utils.gone
import com.cas.musicplayer.utils.observe
import com.cas.musicplayer.utils.visible
import com.cas.musicplayer.viewmodel.viewModel
import kotlinx.android.synthetic.main.fragment_artist_playlists.*


class ArtistPlaylistsFragment : BaseFragment<ArtistPlaylistsViewModel>(), PageableFragment {

    override val layoutResourceId: Int = R.layout.fragment_artist_playlists
    override val viewModel by viewModel { injector.artistPlaylistsViewModel }

    private val artist: Artist by lazy {
        val parcelableArtist = arguments?.getParcelable<Artist>(ArtistFragment.EXTRAS_ARTIST)
        parcelableArtist!!
    }

    private val adapter by lazy {
        ArtistPlaylistsAdapter(artist) { playlist ->
            val bundle = Bundle()
            bundle.putString(PlaylistVideosFragment.EXTRAS_PLAYLIST_ID, playlist.id)
            bundle.putParcelable(ArtistFragment.EXTRAS_ARTIST, artist)
            findNavController().navigate(R.id.playlistVideosFragment, bundle)
        }
    }

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
