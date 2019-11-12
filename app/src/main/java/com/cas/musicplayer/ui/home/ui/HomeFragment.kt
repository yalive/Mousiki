package com.cas.musicplayer.ui.home.ui


import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.cas.musicplayer.R
import com.cas.musicplayer.base.common.Resource
import com.cas.musicplayer.base.common.doOnSuccess
import com.cas.musicplayer.ui.MainActivity
import com.cas.musicplayer.ui.home.domain.model.*
import com.cas.musicplayer.ui.home.ui.adapters.HomeAdapter
import com.cas.musicplayer.ui.home.ui.model.NewReleaseDisplayedItem
import com.cas.musicplayer.utils.Extensions.injector
import com.cas.musicplayer.utils.dpToPixel
import com.cas.musicplayer.utils.gone
import com.cas.musicplayer.utils.observe
import com.cas.musicplayer.utils.visible
import com.cas.musicplayer.viewmodel.viewModel
import com.google.android.material.appbar.CollapsingToolbarLayout
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment(), HomeAdapter.OnMoreItemClickListener {
    private val TAG = "HomeFragment"
    private val handler = Handler()
    private lateinit var adapter: HomeAdapter
    private val viewModel by viewModel { injector.homeViewModel }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val gridLayoutManager = GridLayoutManager(requireContext(), 3)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return 3
            }
        }
        val collapsingToolbar =
            activity?.findViewById<CollapsingToolbarLayout>(R.id.collapsingToolbar)

        val rltContainer = activity?.findViewById<RelativeLayout>(R.id.rltContainer)

        rltContainer?.gone()

        collapsingToolbar?.isTitleEnabled = false

        adapter = HomeAdapter(homeListItems(), {
            val mainActivity = requireActivity() as MainActivity
            mainActivity.collapseBottomPanel()
        }, this)

        recyclerView.layoutManager = gridLayoutManager
        recyclerView.adapter = adapter
        val spacingDp = requireActivity().dpToPixel(8f)
        val marginDp = requireActivity().dpToPixel(8f)
        recyclerView.addItemDecoration(HomeListSpacingItemDecoration(spacingDp, marginDp))
        autoScrollFeaturedVideos()
        observeViewModel()
    }


    private val autoScrollRunnable = Runnable {
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

    private fun observeViewModel() {
        observe(viewModel.trendingTracks) { resource ->
            updateTrending(resource)
        }
        observe(viewModel.charts) { resource ->
            resource.doOnSuccess {
                adapter.charts = it
            }
        }
        observe(viewModel.genres) { resource ->
            resource.doOnSuccess {
                adapter.genres = it
            }
        }
        observe(viewModel.artists) { resource ->
            resource.doOnSuccess {
                adapter.artists = it
            }
        }
    }

    private fun updateTrending(resource: Resource<List<NewReleaseDisplayedItem>>) = when (resource) {
        is Resource.Loading -> {
            txtError.gone()
            progressBar.visible()
            recyclerView.gone()
        }
        is Resource.Failure -> {
            txtError.visible()
            progressBar.gone()
            recyclerView.gone()
        }
        is Resource.Success -> {
            txtError.gone()
            progressBar.gone()
            recyclerView.visible()
            adapter.newReleaseItems = resource.data
        }
    }

    private fun homeListItems(): MutableList<HomeItem> {
        return mutableListOf(
            FeaturedItem,
            HeaderItem("NEW RELEASE"),
            NewReleaseItem,
            HeaderItem("CHARTS"),
            ChartItem,
            HeaderItem("ARTIST"),
            ArtistItem,
            HeaderItem("GENRES"),
            GenreItem
        )
    }

    override fun onMoreItemClick(headerItem: HeaderItem) {
        if (headerItem.title.equals("New Release", true)) {
            findNavController().navigate(R.id.newReleaseFragment)
        } else if (headerItem.title.equals("ARTIST", true)) {
            findNavController().navigate(R.id.artistsFragment)
        } else if (headerItem.title.equals("Genres", true)) {
            findNavController().navigate(R.id.genresFragment)
        } else if (headerItem.title.equals("Charts", true)) {
            findNavController().navigate(R.id.chartsFragment)
        }
    }
}
