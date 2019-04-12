package com.secureappinc.musicplayer.ui.detailcategory.videos


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.secureappinc.musicplayer.R
import com.secureappinc.musicplayer.models.YTCategoryMusicRS
import com.secureappinc.musicplayer.models.YTCategoryMusictem
import com.secureappinc.musicplayer.models.enteties.MusicTrack
import com.secureappinc.musicplayer.net.ApiManager
import com.secureappinc.musicplayer.ui.MainViewModel
import com.secureappinc.musicplayer.ui.detailcategory.DetailGenreFragment
import com.secureappinc.musicplayer.ui.home.models.GenreMusic
import kotlinx.android.synthetic.main.fragment_genre_videos.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class GenreVideosFragment : Fragment() {

    val TAG = "DetailCategoryFragment"


    lateinit var adapter: GenreVideosAdapter
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
        adapter = GenreVideosAdapter(
            listOf(),
            genreMusic,
            ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java)
        )
        recyclerView.adapter = adapter
        laodCategoryMusic()
    }

    private fun laodCategoryMusic() {

        ApiManager.api.getCategoryMusic(genreMusic.topicId, "MA").enqueue(object : Callback<YTCategoryMusicRS> {
            override fun onResponse(call: Call<YTCategoryMusicRS>, response: Response<YTCategoryMusicRS>) {
                if (response.isSuccessful) {
                    val listMusics = response.body()?.items
                    listMusics?.let {

                        val tracks: List<MusicTrack> = createTracksListFrom(listMusics)
                        adapter.items = tracks
                    }
                }
            }

            override fun onFailure(call: Call<YTCategoryMusicRS>, t: Throwable) {
                Log.d(TAG, "")
            }
        })
    }

    private fun createTracksListFrom(listMusics: List<YTCategoryMusictem>): List<MusicTrack> {
        val tracks: MutableList<MusicTrack> = mutableListOf()
        for (musicItem in listMusics) {
            val track =
                MusicTrack(musicItem.id.videoId, musicItem.snippet.title, musicItem.contentDetails.duration)
            tracks.add(track)
        }
        return tracks

    }
}
