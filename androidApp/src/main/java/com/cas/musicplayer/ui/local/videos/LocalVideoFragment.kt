package com.cas.musicplayer.ui.local.videos

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.LocalVideoFragmentBinding
import com.cas.musicplayer.delegateadapter.MousikiAdapter
import com.cas.musicplayer.di.Injector
import com.cas.musicplayer.tmp.observe
import com.cas.musicplayer.ui.base.BaseFragment
import com.cas.musicplayer.ui.bottomsheet.SortVideoByFragment
import com.cas.musicplayer.ui.local.StoragePermissionDelegate
import com.cas.musicplayer.ui.local.StoragePermissionDelegateImpl
import com.cas.musicplayer.ui.local.videos.player.VideoPlayerActivity
import com.cas.musicplayer.ui.local.videos.player.VideoQueueType
import com.cas.musicplayer.ui.local.videos.settings.LocalVideosSettingsFragment
import com.cas.musicplayer.utils.PreferenceUtil
import com.cas.musicplayer.utils.readStoragePermissionsGranted
import com.cas.musicplayer.utils.viewBinding
import com.mousiki.shared.domain.models.DisplayableItem
import com.mousiki.shared.domain.models.Track
import com.mousiki.shared.ui.resource.Resource

class LocalVideoFragment : BaseFragment<LocalVideoViewModel>(
    R.layout.local_video_fragment
), StoragePermissionDelegate by StoragePermissionDelegateImpl() {

    override val screenName: String = "LocalVideoFragment"
    override val viewModel by viewModel { Injector.localVideoViewModel }

    private val binding by viewBinding(LocalVideoFragmentBinding::bind)


    private fun playVideo(track: Track) {
        viewModel.onPlayVideo(track)
        VideoPlayerActivity.start(requireContext(), track, VideoQueueType.AllVideos)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = LocalVideoAdapter(
            onClickTrack = { playVideo(it) },
            onSortClicked = { saveAndSetOrder() },
            onFilterClicked = { showFilterScreen() },
            showCountsAndSortButton = true,
            showFilter = true
        )
        binding.localVideosRecyclerViewView.adapter = adapter

        registerForActivityResult(
            this,
            binding.localVideosRecyclerViewView,
            binding.storagePermissionView
        )

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            observe(viewModel.localSongs) {
                updateUI(it)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkStoragePermission {
            viewModel.loadAllVideos()
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
                    localVideosRecyclerViewView.isVisible = false
                } else {
                    localVideosRecyclerViewView.isVisible = true
                    val adapter = localVideosRecyclerViewView.adapter as MousikiAdapter
                    adapter.submitList(resource.data)
                }
            }
            is Resource.Failure -> {
            }
        }
    }

    private fun saveAndSetOrder() {
        SortVideoByFragment.present(childFragmentManager) { currentSortOrder ->
            PreferenceUtil.videoSortOrder = currentSortOrder
            viewModel.loadAllVideos()
        }
    }

    private fun showFilterScreen() {
        LocalVideosSettingsFragment.present(requireActivity() as AppCompatActivity) {
            viewModel.loadAllVideos()
        }
    }
}