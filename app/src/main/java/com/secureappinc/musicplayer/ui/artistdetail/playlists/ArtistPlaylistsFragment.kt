package com.secureappinc.musicplayer.ui.artistdetail.playlists


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.secureappinc.musicplayer.R
import com.secureappinc.musicplayer.models.Artist
import com.secureappinc.musicplayer.models.Status
import com.secureappinc.musicplayer.ui.artistdetail.ArtistFragment
import com.secureappinc.musicplayer.ui.artistdetail.detailplaylist.PlaylistVideosFragment
import com.secureappinc.musicplayer.utils.gone
import com.secureappinc.musicplayer.utils.visible
import kotlinx.android.synthetic.main.fragment_artist_playlists.*


class ArtistPlaylistsFragment : Fragment() {

    val TAG = "DetailCategoryFragment"


    lateinit var adapter: ArtistPlaylistsAdapter
    lateinit var artist: Artist

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_artist_playlists, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

        val viewModel = ViewModelProviders.of(this).get(ArtistPlaylistsViewModel::class.java)

        viewModel.loadPlaylist(artist.channelId)

        viewModel.searchResultList.observe(this, Observer {
            if (it.status == Status.LOADING) {

            } else if (it.status == Status.SUCCESS) {
                showSuccess()
                adapter.items = it.data!!
            } else if (it.status == Status.ERROR) {
                showError()
            }
        })
    }

    fun showSuccess() {
        progressBar?.gone()
        recyclerView?.visible()
        txtError?.gone()
    }

    fun showError() {
        progressBar?.gone()
        recyclerView?.gone()
        txtError?.visible()
    }
}
