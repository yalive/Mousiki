package com.cas.musicplayer.ui.home.ui


import android.os.Bundle
import android.widget.RelativeLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.cas.musicplayer.R
import com.cas.musicplayer.ui.BaseFragment
import com.cas.musicplayer.ui.home.ui.adapters.HomeAdapter
import com.cas.musicplayer.utils.Extensions.injector
import com.cas.musicplayer.utils.dpToPixel
import com.cas.musicplayer.utils.gone
import com.cas.musicplayer.utils.observe
import com.cas.musicplayer.utils.uiCoroutine
import com.cas.musicplayer.viewmodel.viewModel
import com.google.android.material.appbar.CollapsingToolbarLayout
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.delay


class HomeFragment : BaseFragment<HomeViewModel>() {

    override val viewModel by viewModel { injector.homeViewModel }
    override val layoutResourceId: Int = R.layout.fragment_home

    private val homeAdapter = HomeAdapter {
        mainActivity()?.collapseBottomPanel()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val gridLayoutManager = GridLayoutManager(requireContext(), 3)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int) = 3
        }
        val collapsingToolbar = activity?.findViewById<CollapsingToolbarLayout>(R.id.collapsingToolbar)
        val rltContainer = activity?.findViewById<RelativeLayout>(R.id.rltContainer)
        rltContainer?.gone()
        collapsingToolbar?.isTitleEnabled = false

        recyclerView.apply {
            layoutManager = gridLayoutManager
            adapter = homeAdapter
            val spacingDp = requireActivity().dpToPixel(8f)
            val marginDp = requireActivity().dpToPixel(8f)
            addItemDecoration(HomeListSpacingItemDecoration(spacingDp, marginDp))
        }
        autoScrollFeaturedVideos()
        observeViewModel()
    }

    private fun autoScrollFeaturedVideos() = uiCoroutine {
        while (true) {
            delay(timeMillis = 10 * 1000)
            homeAdapter.autoScrollFeaturedVideos()
        }
    }

    private fun observeViewModel() {
        observe(viewModel.newReleases, homeAdapter::updateNewRelease)
        observe(viewModel.charts, homeAdapter::updateCharts)
        observe(viewModel.genres, homeAdapter::updateGenres)
        observe(viewModel.artists, homeAdapter::updateArtists)
    }
}
