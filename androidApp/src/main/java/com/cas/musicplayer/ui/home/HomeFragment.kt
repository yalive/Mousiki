package com.cas.musicplayer.ui.home


import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.dpToPixel
import com.cas.common.extensions.isDarkMode
import com.cas.common.recyclerview.MarginItemDecoration
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
import com.cas.musicplayer.ui.common.ads.AdsItem
import com.cas.musicplayer.ui.home.adapters.HomeAdapter
import com.cas.musicplayer.utils.DeviceInset
import com.cas.musicplayer.utils.viewBinding
import com.facebook.ads.*
import com.mousiki.shared.ui.home.HomeViewModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants


class HomeFragment : BaseFragment<HomeViewModel>(
    R.layout.fragment_home
) {
    override val screenName: String = "HomeFragment"
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.run {
            addItemDecoration(HomeMarginItemDecoration())
            adapter = homeAdapter
            enforceSingleScrollDirection()
            observe(DeviceInset) { inset ->
                updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    topMargin = inset.top
                }
            }
            addItemDecoration(MarginItemDecoration(
                topMarginProvider = { position ->
                    when (viewModel.homeItems.value?.get(position)) {
                        is AdsItem -> dpToPixel(32)
                        else -> 0
                    }
                }
            ))
        }

        observeViewModel()
        adjustStatusBarWithTheme()
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
}
