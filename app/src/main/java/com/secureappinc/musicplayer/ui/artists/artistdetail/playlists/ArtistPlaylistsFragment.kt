package com.secureappinc.musicplayer.ui.artists.artistdetail.playlists


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.secureappinc.musicplayer.R
import com.secureappinc.musicplayer.data.models.Artist
import com.secureappinc.musicplayer.base.common.Status
import com.secureappinc.musicplayer.ui.BaseFragment
import com.secureappinc.musicplayer.ui.artists.artistdetail.ArtistFragment
import com.secureappinc.musicplayer.ui.artists.artistdetail.detailplaylist.PlaylistVideosFragment
import com.secureappinc.musicplayer.utils.gone
import com.secureappinc.musicplayer.utils.visible
import kotlinx.android.synthetic.main.fragment_artist_playlists.*
import javax.inject.Inject


class ArtistPlaylistsFragment : BaseFragment() {

    val TAG = "DetailCategoryFragment"


    lateinit var adapter: ArtistPlaylistsAdapter
    lateinit var artist: Artist

    @Inject
    lateinit var viewModelFactory: ArtistPlaylistsViewModelFactory

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_artist_playlists, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        app().appComponent.inject(this)
        val parcelableGenre = arguments?.getParcelable<Artist>(ArtistFragment.EXTRAS_ARTIST)
        if (parcelableGenre == null) {
            requireActivity().onBackPressed()
            return
        }
        artist = parcelableGenre
        adapter = ArtistPlaylistsAdapter(listOf(), artist) { playlist ->
            val bundle = Bundle()
            bundle.putString(PlaylistVideosFragment.EXTRAS_PLAYLIST_ID, playlist.id)
            bundle.putParcelable(ArtistFragment.EXTRAS_ARTIST, artist)
            findNavController().navigate(R.id.playlistVideosFragment, bundle)
        }
        recyclerView.adapter = adapter
        loadPlaylist()
    }

    private fun loadPlaylist() {

        val viewModel = ViewModelProviders.of(this, viewModelFactory).get(ArtistPlaylistsViewModel::class.java)

        viewModel.loadPlaylist(artist.channelId)

        viewModel.playlists.observe(this, Observer {
            when (it.status) {
                Status.LOADING -> {

                }
                Status.SUCCESS -> {
                    showSuccess()
                    adapter.items = it.data!!
                }
                Status.ERROR -> showError()
            }
        })
    }

    private fun showSuccess() {
        progressBar?.gone()
        recyclerView?.visible()
        txtError?.gone()
    }

    private fun showError() {
        progressBar?.gone()
        recyclerView?.gone()
        txtError?.visible()
    }
}
