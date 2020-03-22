package com.cas.musicplayer.ui.home


import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.navigation.fragment.findNavController
import com.cas.common.adapter.PageableFragment
import com.cas.common.extensions.observe
import com.cas.common.fragment.BaseFragment
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.di.injector.injector
import com.cas.musicplayer.ui.MainActivity
import com.cas.musicplayer.ui.home.adapters.HomeAdapter
import com.cas.musicplayer.utils.VideoEmplacementLiveData
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : BaseFragment<HomeViewModel>(), PageableFragment {

    override val viewModel by viewModel { injector.homeViewModel }
    override val layoutResourceId: Int = R.layout.fragment_home
    override val screenTitle: String by lazy {
        getString(R.string.app_name)
    }

    private val homeAdapter = HomeAdapter { track ->
        (activity as? MainActivity)?.collapseBottomPanel()
        viewModel.onClickTrack(track)
        VideoEmplacementLiveData.bottom()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recyclerView.addItemDecoration(HomeMarginItemDecoration())
        recyclerView.adapter = homeAdapter
        observeViewModel()
    }

    override fun getPageTitle(): String = "Discover"

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_home, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menuSetting) {
            findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun observeViewModel() {
        observe(viewModel.newReleases, homeAdapter::updatePopularSongs)
        observe(viewModel.charts, homeAdapter::updateCharts)
        observe(viewModel.genres, homeAdapter::updateGenres)
        observe(viewModel.artists, homeAdapter::updateArtists)
    }
}
