package com.secureappinc.musicplayer.ui.artistdetail.detailplaylist


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.secureappinc.musicplayer.R
import com.secureappinc.musicplayer.models.Artist
import com.secureappinc.musicplayer.models.Resource
import com.secureappinc.musicplayer.models.Status
import com.secureappinc.musicplayer.models.enteties.MusicTrack
import com.secureappinc.musicplayer.ui.MainActivity
import com.secureappinc.musicplayer.ui.artistdetail.ArtistFragment
import com.secureappinc.musicplayer.ui.detailcategory.DetailGenreFragment
import com.secureappinc.musicplayer.utils.gone
import com.secureappinc.musicplayer.utils.visible
import kotlinx.android.synthetic.main.fragment_genre_videos.*


class PlaylistVideosFragment : Fragment() {

    val TAG = "DetailCategoryFragment"

    companion object {
        val EXTRAS_PLAYLIST_ID = "playlist_id"
    }

    lateinit var adapter: PlaylistVideosAdapter
    lateinit var artist: Artist
    lateinit var playlistId: String

    lateinit var viewModel: PlaylistVideosViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_artist_videos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val parcelableGenre = arguments?.getParcelable<Artist>(ArtistFragment.EXTRAS_ARTIST)
        playlistId = arguments?.getString(EXTRAS_PLAYLIST_ID)!!
        if (parcelableGenre == null) {
            requireActivity().onBackPressed()
            return
        }

        viewModel = ViewModelProviders.of(this).get(PlaylistVideosViewModel::class.java)

        artist = parcelableGenre
        adapter = PlaylistVideosAdapter(
            listOf(),
            artist
        ) {
            val mainActivity = requireActivity() as MainActivity
            mainActivity.showBottomPanel()
        }
        recyclerView.adapter = adapter

        viewModel.searchResultList.observe(this, Observer { resource ->
            updateUI(resource)
        })

        loadTracks()

        requireActivity().title = artist.name
    }

    private fun updateUI(resource: Resource<List<MusicTrack>>) {
        when (resource.status) {
            Status.SUCCESS -> {
                adapter.items = resource.data!!
                recyclerView.visible()
                progressBar.gone()
                txtError.gone()
            }
            Status.ERROR -> {
                progressBar.gone()
                txtError.visible()
            }
            Status.LOADING -> {
                progressBar.visible()
                txtError.gone()
            }
        }
    }

    private fun loadTracks() {
        viewModel.getPlaylistVideos(playlistId)
    }
}
