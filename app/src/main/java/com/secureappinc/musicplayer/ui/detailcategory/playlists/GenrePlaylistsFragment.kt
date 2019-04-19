package com.secureappinc.musicplayer.ui.detailcategory.playlists


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.secureappinc.musicplayer.R
import com.secureappinc.musicplayer.models.YTTrendingMusicRS
import com.secureappinc.musicplayer.net.ApiManager
import com.secureappinc.musicplayer.net.YoutubeApi.Companion.DUMMY_CHANNEL_ID
import com.secureappinc.musicplayer.ui.detailcategory.DetailGenreFragment
import com.secureappinc.musicplayer.ui.home.models.GenreMusic
import com.secureappinc.musicplayer.utils.gone
import com.secureappinc.musicplayer.utils.visible
import kotlinx.android.synthetic.main.fragment_genre_videos.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class GenrePlaylistsFragment : Fragment() {

    val TAG = "DetailCategoryFragment"


    lateinit var adapter: GenrePlaylistsAdapter
    lateinit var genreMusic: GenreMusic

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_genre_videos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val parcelableGenre = arguments?.getParcelable<GenreMusic>(DetailGenreFragment.EXTRAS_GENRE)
        if (parcelableGenre == null) {
            requireActivity().onBackPressed()
            return
        }
        genreMusic = parcelableGenre
        adapter = GenrePlaylistsAdapter(listOf(), genreMusic)
        recyclerView.adapter = adapter
        loadPlaylist()
    }

    private fun loadPlaylist() {

        ApiManager.api.getPlaylist(DUMMY_CHANNEL_ID, "MA").enqueue(object : Callback<YTTrendingMusicRS> {
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
        progressBar.gone()
        recyclerView.visible()
        txtError.gone()
    }

    fun showError() {
        progressBar.gone()
        recyclerView.gone()
        txtError.visible()
    }
}
