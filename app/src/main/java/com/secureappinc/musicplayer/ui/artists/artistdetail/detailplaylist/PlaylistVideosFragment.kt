package com.secureappinc.musicplayer.ui.artists.artistdetail.detailplaylist


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.secureappinc.musicplayer.R
import com.secureappinc.musicplayer.base.common.Resource
import com.secureappinc.musicplayer.base.common.Status
import com.secureappinc.musicplayer.data.enteties.MusicTrack
import com.secureappinc.musicplayer.data.models.Artist
import com.secureappinc.musicplayer.ui.BaseFragment
import com.secureappinc.musicplayer.ui.MainActivity
import com.secureappinc.musicplayer.ui.artists.artistdetail.ArtistFragment
import com.secureappinc.musicplayer.ui.bottomsheet.FvaBottomSheetFragment
import com.secureappinc.musicplayer.utils.Extensions.injector
import com.secureappinc.musicplayer.utils.gone
import com.secureappinc.musicplayer.utils.visible
import com.secureappinc.musicplayer.viewmodel.viewModel
import kotlinx.android.synthetic.main.fragment_genre_videos.*


class PlaylistVideosFragment : BaseFragment() {

    val TAG = "DetailCategoryFragment"

    companion object {
        val EXTRAS_PLAYLIST_ID = "playlist_id"
    }

    lateinit var adapter: PlaylistVideosAdapter
    lateinit var artist: Artist
    lateinit var playlistId: String

    private val viewModel by viewModel { injector.playlistVideosViewModel }

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

        artist = parcelableGenre
        adapter = PlaylistVideosAdapter(
            listOf(),
            artist
        ) {
            val mainActivity = requireActivity() as MainActivity
            mainActivity.collapseBottomPanel()
        }
        recyclerView.adapter = adapter

        viewModel.searchResultList.observe(this, Observer { resource ->
            updateUI(resource)
        })

        adapter.onClickMore = { track ->
            showBottomMenuButtons(track)
        }

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

    private fun showBottomMenuButtons(musicTrack: MusicTrack) {
        val bottomSheetFragment = FvaBottomSheetFragment()
        val bundle = Bundle()
        bundle.putString("MUSIC_TRACK", injector.gson.toJson(musicTrack))
        bottomSheetFragment.arguments = bundle
        bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
    }
}
