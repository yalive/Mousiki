package com.secureappinc.musicplayer.ui.home


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.secureappinc.musicplayer.R
import com.secureappinc.musicplayer.models.YTTrendingItem
import com.secureappinc.musicplayer.models.YTTrendingMusicRS
import com.secureappinc.musicplayer.models.enteties.MusicTrack
import com.secureappinc.musicplayer.net.ApiManager
import com.secureappinc.musicplayer.net.YoutubeApi
import com.secureappinc.musicplayer.ui.MainActivity
import com.secureappinc.musicplayer.ui.MainViewModel
import com.secureappinc.musicplayer.ui.detailcategory.DetailGenreFragment
import com.secureappinc.musicplayer.ui.home.models.*
import com.secureappinc.musicplayer.utils.dpToPixel
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.android.synthetic.main.fragment_home.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeFragment : Fragment() {

    val TAG = "HomeFragment"

    lateinit var adapter: HomeAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val gridLayoutManager = GridLayoutManager(requireContext(), 3)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val viewType = adapter.getItemViewType(position)
                if (viewType == HomeAdapter.TYPE_GENRE || viewType == HomeAdapter.TYPE_ARTIST) {
                    return 1
                } else {
                    return 3
                }
            }
        }

        adapter = HomeAdapter(mockList(), ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java), {
            if (it is GenreItem) {
                val bundle = Bundle()
                bundle.putParcelable(DetailGenreFragment.EXTRAS_GENRE, it.genre)
                recyclerView.findNavController().navigate(R.id.detailGenreFragment, bundle)
            }
        }, {
            val mainActivity = requireActivity() as MainActivity
            mainActivity.slidingPaneLayout.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
        })


        recyclerView.layoutManager = gridLayoutManager
        recyclerView.adapter = adapter
        val spacingDp = requireActivity().dpToPixel(8f)
        val marginDp = requireActivity().dpToPixel(8f)
        recyclerView.addItemDecoration(GridSpacingItemDecoration(spacingDp, marginDp))
        //recyclerView.isNestedScrollingEnabled = true

        loadTrendingMusic()
    }


    private fun loadTrendingMusic() {
        ApiManager.api.getTrending(YoutubeApi.TRENDING).enqueue(object : Callback<YTTrendingMusicRS> {
            override fun onResponse(call: Call<YTTrendingMusicRS>, response: Response<YTTrendingMusicRS>) {
                if (response.isSuccessful) {
                    val listTrendingMusic = response.body()?.items
                    listTrendingMusic?.let {
                        val tracks: List<MusicTrack> = createTracksListFrom(listTrendingMusic)
                        adapter.tracks = tracks
                    }
                }
            }

            override fun onFailure(call: Call<YTTrendingMusicRS>, t: Throwable) {
                Log.d(TAG, "")
            }
        })

    }

    private fun createTracksListFrom(listTrendingYutube: List<YTTrendingItem>): List<MusicTrack> {
        val tracks: MutableList<MusicTrack> = mutableListOf()
        for (ytTrendingItem in listTrendingYutube) {
            val track =
                MusicTrack(ytTrendingItem.id, ytTrendingItem.snippet.title, ytTrendingItem.contentDetails.duration)
            tracks.add(track)
        }
        return tracks
    }

    fun mockList(): List<HomeItem> {
        val list = mutableListOf<HomeItem>()
        for (i in 0 until 20) {
            if (i == 0) {
                list.add(FeaturedItem(listOf()))
            } else if (i == 1) {
                list.add(HeaderItem("New Release"))
            } else if (i == 2) {
                list.add(NewReleaseItem(listOf()))
            } else if (i == 3) {
                list.add(HeaderItem("ARTIST"))
            } else if (i in 4..9) {
                list.add(ArtistItem(listOf()))
            } else if (i == 10) {
                list.add(HeaderItem("GENRES"))
            } else if (i in 11..19) {
                list.add(GenreItem(GenreMusic.allValues[i - 11]))
            }
        }
        return list
    }
}
