package com.cas.musicplayer.ui.home


import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

    private val homeAdapter by lazy {
        HomeAdapter(viewModel = viewModel) { track ->
            (activity as? MainActivity)?.collapseBottomPanel()
            viewModel.onClickTrack(track)
            VideoEmplacementLiveData.bottom(true)
        }
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
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                adjustStatusBar()
            }
        })
        adjustStatusBar()
        darkStatusBar()
    }

    override fun getPageTitle(): String = getString(R.string.title_discover)

    override fun withToolbar(): Boolean = false

    private fun observeViewModel() {
        observe(viewModel.newReleases, homeAdapter::updatePopularSongs)
        observe(viewModel.charts, homeAdapter::updateCharts)
        observe(viewModel.genres, homeAdapter::updateGenres)
        observe(viewModel.artists, homeAdapter::updateArtists)
    }

    //region Status bar adjusment
    private val headerHeight by lazy {
        resources.getDimensionPixelSize(R.dimen.home_chart_height)
    }
    private val triggerAlpha by lazy {
        3 * headerHeight.toFloat() / 4
    }

    private val triggerFill by lazy {
        headerHeight.toFloat() / 4
    }

    private fun adjustStatusBar() {
        val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
        val firstVisiblePosition = linearLayoutManager.findFirstVisibleItemPosition()
        val rect = Rect()
        linearLayoutManager.findViewByPosition(firstVisiblePosition)?.getGlobalVisibleRect(rect)
        val visibleChartHeight = rect.bottom.toFloat()
        val fillColor = if (isDarkMode()) Color.BLACK else Color.WHITE
        val color = when {
            firstVisiblePosition != 0 || visibleChartHeight < triggerFill -> fillColor.also {
                darkStatusBarOnDarkMode()
            }
            visibleChartHeight in triggerFill..triggerAlpha -> {
                darkStatusBarOnDarkMode()
                val alpha = 255 * (visibleChartHeight - triggerAlpha) / (triggerFill - triggerAlpha)
                ColorUtils.setAlphaComponent(fillColor, alpha.toInt())
            }
            else -> Color.TRANSPARENT.also {
                darkStatusBar()
            }
        }
        requireActivity().window.statusBarColor = color

    }
    //endregion
}
