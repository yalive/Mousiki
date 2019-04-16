package com.secureappinc.musicplayer.ui.home


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.secureappinc.musicplayer.R
import com.secureappinc.musicplayer.models.Resource
import com.secureappinc.musicplayer.models.Status
import com.secureappinc.musicplayer.models.enteties.MusicTrack
import com.secureappinc.musicplayer.ui.MainActivity
import com.secureappinc.musicplayer.ui.MainViewModel
import com.secureappinc.musicplayer.ui.detailcategory.DetailGenreFragment
import com.secureappinc.musicplayer.ui.home.models.*
import com.secureappinc.musicplayer.utils.dpToPixel
import com.secureappinc.musicplayer.utils.gone
import com.secureappinc.musicplayer.utils.visible
import kotlinx.android.synthetic.main.fragment_home.*


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
            mainActivity.showBottomPanel()
        })


        recyclerView.layoutManager = gridLayoutManager
        recyclerView.adapter = adapter
        val spacingDp = requireActivity().dpToPixel(8f)
        val marginDp = requireActivity().dpToPixel(8f)
        recyclerView.addItemDecoration(GridSpacingItemDecoration(spacingDp, marginDp))


        val viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        viewModel.trendingTracks.observe(this, Observer { resource ->
            updateUI(resource)
        })

        viewModel.loadTrendingMusic()
    }


    private fun updateUI(resource: Resource<List<MusicTrack>>) {
        if (resource.status == Status.LOADING) {
            txtError.gone()
            progressBar.visible()
            recyclerView.gone()
        } else if (resource.status == Status.ERROR) {
            txtError.visible()
            progressBar.gone()
            recyclerView.gone()
        } else {
            txtError.gone()
            progressBar.gone()
            recyclerView.visible()
            adapter.tracks = resource.data!!
        }
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
