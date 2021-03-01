package com.cas.musicplayer.ui.home


import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.widget.ProgressBar
import androidx.core.graphics.ColorUtils
import androidx.core.view.isVisible
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.extensions.isDarkMode
import com.cas.common.recyclerview.enforceSingleScrollDirection
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.FragmentHomeBinding
import com.cas.musicplayer.di.Injector
import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.player.services.PlaybackLiveData
import com.cas.musicplayer.tmp.observe
import com.cas.musicplayer.ui.MainActivity
import com.cas.musicplayer.ui.base.BaseFragment
import com.cas.musicplayer.ui.common.songs.HorizontalListSongsAdapterDelegate
import com.cas.musicplayer.ui.home.adapters.HomeAdapter
import com.cas.musicplayer.ui.popular.SongsDiffUtil
import com.cas.musicplayer.utils.viewBinding
import com.mousiki.shared.ui.home.HomeViewModel
import com.mousiki.shared.ui.resource.valueOrNull
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants


class HomeFragment : BaseFragment<HomeViewModel>(
    R.layout.fragment_home
) {
    private val binding by viewBinding(FragmentHomeBinding::bind)

    override val viewModel by viewModel { Injector.homeViewModel }
    override val screenTitle: String by lazy {
        getString(R.string.app_name)
    }

    private val recyclerView: RecyclerView
        get() = binding.recyclerView

    private val progressBar: ProgressBar
        get() = binding.progressBar

    private val homeAdapter by lazy {
        HomeAdapter(viewModel = viewModel, onVideoSelected = { track, tracks ->
            (activity as? MainActivity)?.collapseBottomPanel()
            viewModel.onClickTrack(track, tracks)
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
        recyclerView.run {
            addItemDecoration(HomeMarginItemDecoration())
            adapter = homeAdapter
            enforceSingleScrollDirection()
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    adjustStatusBar()
                }
            })
        }

        observeViewModel()
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
        val items = viewModel.newReleases.valueOrNull() ?: return
        val updatedList = items.map { item ->
            val isCurrent = PlayerQueue.value?.youtubeId == item.track.youtubeId
                    && state != PlayerConstants.PlayerState.UNKNOWN
            item.copy(
                isCurrent = isCurrent,
                isPlaying = isCurrent && (state == PlayerConstants.PlayerState.PLAYING || state == PlayerConstants.PlayerState.BUFFERING)
            )
        }

        recyclerView.post {
            val holder = recyclerView.findViewHolderForAdapterPosition(2)
                    as? HorizontalListSongsAdapterDelegate.HorizontalSongsListViewHolder
            if (holder != null) {
                val adapter = holder.adapter
                val diffCallback = SongsDiffUtil(adapter.dataItems, updatedList)
                adapter.submitList(updatedList, diffCallback)
            }
        }

    }

    override fun withToolbar(): Boolean = false

    private fun observeViewModel() {
        observe(viewModel.homeItems.asLiveData()) { items ->
            if (items == null) return@observe
            homeAdapter.dataItems = items.toMutableList()
            progressBar.isVisible = false
        }
        observe(viewModel.genres.asLiveData(), homeAdapter::updateGenres)
        observe(viewModel.artists.asLiveData(), homeAdapter::updateArtists)
        observe(viewModel.newReleases.asLiveData(), homeAdapter::updatePopularSongs)
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
