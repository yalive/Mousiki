package com.secureappinc.musicplayer.ui.artistdetail.playlists


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.secureappinc.musicplayer.R
import com.secureappinc.musicplayer.models.Artist
import com.secureappinc.musicplayer.models.YTTrendingMusicRS
import com.secureappinc.musicplayer.net.ApiManager
import com.secureappinc.musicplayer.ui.artistdetail.ArtistFragment
import com.secureappinc.musicplayer.ui.artistdetail.detailplaylist.PlaylistVideosFragment
import com.secureappinc.musicplayer.utils.gone
import com.secureappinc.musicplayer.utils.visible
import kotlinx.android.synthetic.main.fragment_artist_playlists.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


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

        ApiManager.api.getPlaylist(artist.channelId, "MA").enqueue(object : Callback<YTTrendingMusicRS> {
            override fun onResponse(call: Call<YTTrendingMusicRS>, response: Response<YTTrendingMusicRS>) {
                if (response.isSuccessful) {
                    showSuccess()
                    val listMusics = response.body()?.items
                    listMusics?.let {
                        adapter.items = listMusics
                    }
                } else {
                    showError()
                }
            }

            override fun onFailure(call: Call<YTTrendingMusicRS>, t: Throwable) {
                Log.d(TAG, "")
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
