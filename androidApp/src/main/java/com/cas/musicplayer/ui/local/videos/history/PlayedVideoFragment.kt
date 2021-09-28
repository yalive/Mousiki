package com.cas.musicplayer.ui.local.videos.history

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.PlayedVideoFragmentBinding
import com.cas.musicplayer.delegateadapter.MousikiAdapter
import com.cas.musicplayer.di.Injector
import com.cas.musicplayer.tmp.observe
import com.cas.musicplayer.ui.base.BaseFragment
import com.cas.musicplayer.ui.local.StoragePermissionDelegate
import com.cas.musicplayer.ui.local.StoragePermissionDelegateImpl
import com.cas.musicplayer.ui.local.videos.LocalVideoAdapter
import com.cas.musicplayer.ui.local.videos.player.VideoPlayerActivity
import com.cas.musicplayer.ui.local.videos.player.VideoQueueType
import com.cas.musicplayer.utils.readStoragePermissionsGranted
import com.cas.musicplayer.utils.viewBinding
import com.mousiki.shared.domain.models.DisplayableItem
import com.mousiki.shared.domain.models.Track
import com.mousiki.shared.ui.resource.Resource

class PlayedVideoFragment : BaseFragment<PlayedVideoViewModel>(
    R.layout.played_video_fragment
), StoragePermissionDelegate by StoragePermissionDelegateImpl() {

    override val screenName: String = "PlayedVideoFragment"
    override val viewModel by viewModel { Injector.playedVideoViewModel }

    private val binding by viewBinding(PlayedVideoFragmentBinding::bind)

    private fun playVideo(track: Track) {
        viewModel.onPlayVideo(track)
        VideoPlayerActivity.start(requireContext(), track, VideoQueueType.History)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = LocalVideoAdapter(
            onClickTrack = { playVideo(it) },
            onSortClicked = { },
            onFilterClicked = { },
            showCountsAndSortButton = false,
            showFilter = false,
            isFromHistory = true
        )
        binding.localVideosRecyclerViewView.adapter = adapter

        registerForActivityResult(
            this,
            binding.localVideosRecyclerViewView,
            binding.storagePermissionView
        )

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            observe(viewModel.playedVideos) {
                updateUI(it)
            }
        }
    }

    private fun updateUI(resource: Resource<List<DisplayableItem>>) = with(binding) {
        if (!readStoragePermissionsGranted()) {
            return
        }
        when (resource) {
            Resource.Loading -> {
                shimmerView.loadingView.isVisible = true
                shimmerView.loadingView.startShimmer()
                shimmerView.loadingView.alpha = 1f
            }
            is Resource.Success -> {
                shimmerView.loadingView.alpha = 0f
                shimmerView.loadingView.stopShimmer()
                shimmerView.loadingView.isVisible = false
                if (resource.data.isNullOrEmpty()) {
                    emptyView.isVisible = true
                    localVideosRecyclerViewView.isVisible = false
                } else {
                    emptyView.isVisible = false
                    localVideosRecyclerViewView.isVisible = true
                    val adapter = localVideosRecyclerViewView.adapter as MousikiAdapter
                    adapter.submitList(resource.data)
                }
            }
            is Resource.Failure -> {
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkStoragePermission {
            viewModel.getAllPlayedVideos()
        }
    }
}