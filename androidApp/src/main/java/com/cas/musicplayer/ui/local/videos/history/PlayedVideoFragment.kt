package com.cas.musicplayer.ui.local.videos.history

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.LocalVideoFragmentBinding
import com.cas.musicplayer.di.Injector
import com.cas.musicplayer.tmp.observe
import com.cas.musicplayer.ui.base.BaseFragment
import com.cas.musicplayer.ui.local.StoragePermissionDelegate
import com.cas.musicplayer.ui.local.StoragePermissionDelegateImpl
import com.cas.musicplayer.ui.local.videos.LocalVideoAdapter
import com.cas.musicplayer.ui.local.videos.player.VideoPlayerActivity
import com.cas.musicplayer.utils.viewBinding
import com.mousiki.shared.domain.models.Track

class PlayedVideoFragment : BaseFragment<PlayedVideoViewModel>(
    R.layout.local_video_fragment
), StoragePermissionDelegate by StoragePermissionDelegateImpl() {

    override val screenName: String = "PlayedVideoFragment"
    override val viewModel by viewModel { Injector.playedVideoViewModel }

    private val binding by viewBinding(LocalVideoFragmentBinding::bind)

    private val adapter by lazy {
        LocalVideoAdapter(
            onClickTrack = { playVideo(it) },
            onSortClicked = { },
            onFilterClicked = { },
            showCountsAndSortButton = false,
            showFilter = false
        )
    }

    private fun playVideo(track: Track) {
        viewModel.onPlayVideo(track)
        val intent = Intent(activity, VideoPlayerActivity::class.java)
        intent.putExtra("video_type", track.type)
        intent.putExtra("video_id", track.id.toLong())
        intent.putExtra("video_name", track.title)
        startActivity(intent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.localVideosRecyclerViewView.adapter = adapter

        registerForActivityResult(
            this,
            binding.localVideosRecyclerViewView,
            binding.storagePermissionView
        )

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            observe(viewModel.playedVideos) {
                adapter.submitList(it)
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