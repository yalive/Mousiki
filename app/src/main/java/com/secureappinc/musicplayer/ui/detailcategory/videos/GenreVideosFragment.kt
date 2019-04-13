package com.secureappinc.musicplayer.ui.detailcategory.videos


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.secureappinc.musicplayer.R
import com.secureappinc.musicplayer.models.Resource
import com.secureappinc.musicplayer.models.Status
import com.secureappinc.musicplayer.models.YTCategoryMusictem
import com.secureappinc.musicplayer.models.enteties.MusicTrack
import com.secureappinc.musicplayer.ui.MainActivity
import com.secureappinc.musicplayer.ui.detailcategory.DetailGenreFragment
import com.secureappinc.musicplayer.ui.home.models.GenreMusic
import com.secureappinc.musicplayer.utils.gone
import com.secureappinc.musicplayer.utils.visible
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.android.synthetic.main.fragment_genre_videos.*


class GenreVideosFragment : Fragment() {

    val TAG = "DetailCategoryFragment"


    lateinit var adapter: GenreVideosAdapter
    lateinit var genreMusic: GenreMusic

    lateinit var viewModel: GenreVideosViewModel

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

        viewModel = ViewModelProviders.of(this).get(GenreVideosViewModel::class.java)

        genreMusic = parcelableGenre
        adapter = GenreVideosAdapter(
            listOf(),
            genreMusic
        ) {
            val mainActivity = requireActivity() as MainActivity
            mainActivity.slidingPaneLayout.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
        }
        recyclerView.adapter = adapter

        viewModel.searchResultList.observe(this, Observer { resource ->
            updateUI(resource)
        })

        laodCategoryMusic()
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

    private fun laodCategoryMusic() {
        viewModel.loadVideosForTopic(genreMusic.topicId)
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
