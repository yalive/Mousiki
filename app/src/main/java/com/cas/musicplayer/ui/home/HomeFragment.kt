package com.cas.musicplayer.ui.home


import android.os.Bundle
import android.widget.RelativeLayout
import com.cas.common.adapter.PageableFragment
import com.cas.common.extensions.gone
import com.cas.common.extensions.observe
import com.cas.common.fragment.BaseFragment
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.di.injector.injector
import com.cas.musicplayer.ui.MainActivity
import com.cas.musicplayer.ui.home.adapters.HomeAdapter
import com.cas.musicplayer.utils.VideoEmplacementLiveData
import com.google.android.material.appbar.CollapsingToolbarLayout
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : BaseFragment<HomeViewModel>(), PageableFragment {

    override val viewModel by viewModel { injector.homeViewModel }
    override val layoutResourceId: Int = R.layout.fragment_home

    private val homeAdapter = HomeAdapter { track ->
        (activity as? MainActivity)?.collapseBottomPanel()
        viewModel.onClickTrack(track)
        VideoEmplacementLiveData.bottom()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recyclerView.addItemDecoration(HomeMarginItemDecoration())
        recyclerView.adapter = homeAdapter
        observeViewModel()
    }

    override fun getPageTitle(): String = "Discover"

    private fun observeViewModel() {
        observe(viewModel.newReleases, homeAdapter::updatePopularSongs)
        observe(viewModel.charts, homeAdapter::updateCharts)
        observe(viewModel.genres, homeAdapter::updateGenres)
        observe(viewModel.artists, homeAdapter::updateArtists)
    }
}
