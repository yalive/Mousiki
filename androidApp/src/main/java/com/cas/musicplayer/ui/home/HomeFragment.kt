package com.cas.musicplayer.ui.home


import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.view.View
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
import com.cas.musicplayer.player.services.PlaybackLiveData
import com.cas.musicplayer.tmp.observe
import com.cas.musicplayer.ui.MainActivity
import com.cas.musicplayer.ui.base.BaseFragment
import com.cas.musicplayer.ui.base.adjustStatusBarWithTheme
import com.cas.musicplayer.ui.base.darkStatusBar
import com.cas.musicplayer.ui.home.adapters.HomeAdapter
import com.cas.musicplayer.utils.viewBinding
import com.facebook.ads.*
import com.mousiki.shared.ui.home.HomeViewModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants


class HomeFragment : BaseFragment<HomeViewModel>(
    R.layout.fragment_home
) {
    private val binding by viewBinding(FragmentHomeBinding::bind)

    override val viewModel by viewModel { Injector.homeViewModel }

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                viewModel.onPlaybackStateChanged()
            }
        }
    }

    private fun observeViewModel() {
        observe(viewModel.homeItems.asLiveData()) { items ->
            if (items == null) return@observe
            homeAdapter.submitList(items)
            progressBar.isVisible = false
        }
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
