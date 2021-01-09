package com.cas.musicplayer.ui.home


import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.extensions.isDarkMode
import com.cas.common.extensions.observe
import com.cas.common.extensions.valueOrNull
import com.cas.common.fragment.BaseFragment
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.di.injector.injector
import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.player.services.PlaybackLiveData
import com.cas.musicplayer.ui.MainActivity
import com.cas.musicplayer.ui.common.songs.HorizontalListSongsAdapterDelegate
import com.cas.musicplayer.ui.home.adapters.HomeAdapter
import com.cas.musicplayer.ui.popular.SongsDiffUtil
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants


class HomeFragment : BaseFragment<HomeViewModel>() {

    override val viewModel by viewModel { injector.homeViewModel }
    override val layoutResourceId: Int = R.layout.fragment_home
    override val screenTitle: String by lazy {
        getString(R.string.app_name)
    }

    private var recyclerView: RecyclerView? = null

    private val homeAdapter by lazy {
        HomeAdapter(viewModel = viewModel, onVideoSelected = { track ->
            (activity as? MainActivity)?.collapseBottomPanel()
            viewModel.onClickTrack(track)
        }, onClickRetryNewRelease = {
            viewModel.onClickRetryNewRelease()
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recyclerView = view?.findViewById(R.id.recyclerView)
        recyclerView?.addItemDecoration(HomeMarginItemDecoration())
        recyclerView?.adapter = homeAdapter
        observeViewModel()
        recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                adjustStatusBar()
            }
        })
        adjustStatusBar()
        darkStatusBar()
        observe(PlaybackLiveData) { state ->
            if (state == PlayerConstants.PlayerState.PLAYING
                || state == PlayerConstants.PlayerState.PAUSED
                || state == PlayerConstants.PlayerState.ENDED
                || state == PlayerConstants.PlayerState.BUFFERING
                || state == PlayerConstants.PlayerState.UNKNOWN
            ) {
                updateCurrentPlayingItem(state)
            }
        }
    }

    private fun updateCurrentPlayingItem(state: PlayerConstants.PlayerState) {
        viewModel.newReleases.valueOrNull()?.let { items ->
            val updatedList = items.map { item ->
                val isCurrent = PlayerQueue.value?.youtubeId == item.track.youtubeId
                        && state != PlayerConstants.PlayerState.UNKNOWN
                item.copy(
                    isCurrent = isCurrent,
                    isPlaying = isCurrent && (state == PlayerConstants.PlayerState.PLAYING || state == PlayerConstants.PlayerState.BUFFERING)
                )
            }

            recyclerView?.post {
                val holder = recyclerView?.findViewHolderForAdapterPosition(2)
                        as? HorizontalListSongsAdapterDelegate.HorizontalSongsListViewHolder
                if (holder != null) {
                    val adapter = holder.adapter
                    val diffCallback = SongsDiffUtil(adapter.dataItems, updatedList)
                    adapter.submitList(updatedList, diffCallback)
                }
            }
        }
    }

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
        val linearLayoutManager = recyclerView?.layoutManager as LinearLayoutManager
        val firstVisiblePosition = linearLayoutManager.findFirstVisibleItemPosition()
        val rect = Rect()
        linearLayoutManager.findViewByPosition(firstVisiblePosition)?.getGlobalVisibleRect(rect)
        val visibleChartHeight = rect.bottom.toFloat()
        val fillColor = if (requireContext().isDarkMode()) Color.BLACK else Color.WHITE
        val color = when {
            firstVisiblePosition != 0 || visibleChartHeight < triggerFill -> fillColor.also {
                adjustStatusBarWithTheme()
            }
            visibleChartHeight in triggerFill..triggerAlpha -> {
                adjustStatusBarWithTheme()
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
