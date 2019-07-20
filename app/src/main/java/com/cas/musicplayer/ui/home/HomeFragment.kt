package com.cas.musicplayer.ui.home


import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.cas.musicplayer.R
import com.cas.musicplayer.base.common.Resource
import com.cas.musicplayer.base.common.Status
import com.cas.musicplayer.data.enteties.MusicTrack
import com.cas.musicplayer.data.models.Artist
import com.cas.musicplayer.ui.MainActivity
import com.cas.musicplayer.ui.artists.artistdetail.ArtistFragment
import com.cas.musicplayer.ui.genres.detailgenre.DetailGenreFragment
import com.cas.musicplayer.ui.home.models.*
import com.cas.musicplayer.utils.Extensions.injector
import com.cas.musicplayer.utils.dpToPixel
import com.cas.musicplayer.utils.getCurrentLocale
import com.cas.musicplayer.utils.gone
import com.cas.musicplayer.utils.visible
import com.cas.musicplayer.viewmodel.viewModel
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment(), HomeAdapter.OnMoreItemClickListener {
    val TAG = "HomeFragment"
    private val handler = Handler()
    lateinit var adapter: HomeAdapter

    private val viewModel by viewModel { injector.homeViewModel }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.cas.musicplayer.R.layout.fragment_home, container, false)
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
        val collapsingToolbar = activity?.findViewById<CollapsingToolbarLayout>(R.id.collapsingToolbar)

        val rltContainer = activity?.findViewById<RelativeLayout>(R.id.rltContainer)

        rltContainer?.gone()

        collapsingToolbar?.isTitleEnabled = false

        adapter =
            HomeAdapter(initializeList(), {
                if (it is GenreItem) {
                    val bundle = Bundle()
                    bundle.putParcelable(DetailGenreFragment.EXTRAS_GENRE, it.genre)
                    findNavController().navigate(com.cas.musicplayer.R.id.detailGenreFragment, bundle)
                } else if (it is ArtistItem) {
                    val bundle = Bundle()
                    bundle.putParcelable(ArtistFragment.EXTRAS_ARTIST, it.artist)
                    findNavController().navigate(com.cas.musicplayer.R.id.artistFragment, bundle)
                }
            }, {
                val mainActivity = requireActivity() as MainActivity
                mainActivity.collapseBottomPanel()
            }, this)


        recyclerView.layoutManager = gridLayoutManager
        recyclerView.adapter = adapter
        val spacingDp = requireActivity().dpToPixel(8f)
        val marginDp = requireActivity().dpToPixel(8f)
        recyclerView.addItemDecoration(GridSpacingItemDecoration(spacingDp, marginDp))

        viewModel.trendingTracks.observe(this, Observer { resource ->
            updateUI(resource)
        })

        viewModel.sixArtists.observe(this, Observer { resource ->
            updateArtists(resource)
        })

        viewModel.loadTrendingMusic()
        viewModel.loadArtists(getCurrentLocale())

        autoScrollFeaturedVideos()
    }


    val autoScrollRunnable = Runnable {
        adapter.autoScrollFeaturedVideos()
        autoScrollFeaturedVideos()
    }


    private fun autoScrollFeaturedVideos() {
        handler.postDelayed(autoScrollRunnable, 10 * 1000)
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(autoScrollRunnable)
    }

    private fun updateArtists(resource: Resource<List<Artist>>) {
        if (resource.status == Status.SUCCESS) {
            val artists = resource.data!!

            val artistItems = artists.map { ArtistItem(it) }

            if (artistItems.size == 6) {
                for (i in 6..11) {
                    adapter.items[i] = artistItems[i - 6]
                }

                adapter.notifyItemRangeChanged(6, 11)
            } else {
                // TODO
            }
        }
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


    private fun initializeList(): MutableList<HomeItem> {
        val list = mutableListOf<HomeItem>()
        for (i in 0 until 22) {
            if (i == 0) {
                list.add(FeaturedItem(listOf()))
            } else if (i == 1) {
                list.add(HeaderItem("NEW RELEASE"))
            } else if (i == 2) {
                list.add(NewReleaseItem(listOf()))
            } else if (i == 3) {
                list.add(HeaderItem("CHARTS"))
            } else if (i == 4) {
                list.add(ChartItem(ChartModel.allValues.take(6).shuffled()))
            } else if (i == 5) {
                list.add(HeaderItem("ARTIST"))
            } else if (i in 6..11) {
                list.add(ArtistItem(artist = Artist("", "", "", "")))
            } else if (i == 12) {
                list.add(HeaderItem("GENRES"))
            } else if (i in 13..21) {
                list.add(GenreItem(GenreMusic.allValues.take(9)[i - 13]))
            }
        }
        return list
    }

    override fun onMoreItemClick(headerItem: HeaderItem) {
        if (headerItem.title.equals("New Release", true)) {
            findNavController().navigate(com.cas.musicplayer.R.id.newReleaseFragment)
        } else if (headerItem.title.equals("ARTIST", true)) {
            findNavController().navigate(com.cas.musicplayer.R.id.artistsFragment)
        } else if (headerItem.title.equals("Genres", true)) {
            findNavController().navigate(com.cas.musicplayer.R.id.genresFragment)
        } else if (headerItem.title.equals("Charts", true)) {
            findNavController().navigate(com.cas.musicplayer.R.id.chartsFragment)
        }
    }
}
