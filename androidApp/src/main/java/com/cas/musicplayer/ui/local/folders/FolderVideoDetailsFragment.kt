package com.cas.musicplayer.ui.local.folders

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.updatePadding
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.FolderDetailsFragmentBinding
import com.cas.musicplayer.di.Injector
import com.cas.musicplayer.player.services.PlaybackLiveData
import com.cas.musicplayer.tmp.observe
import com.cas.musicplayer.ui.base.BaseFragment
import com.cas.musicplayer.ui.base.setupToolbar
import com.cas.musicplayer.ui.local.videos.LocalVideoAdapter
import com.cas.musicplayer.ui.local.videos.player.VideoPlayerActivity
import com.cas.musicplayer.utils.DeviceInset
import com.cas.musicplayer.utils.viewBinding
import com.mousiki.shared.domain.models.Track
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants

class FolderVideoDetailsFragment : BaseFragment<FolderVideoDetailsViewModel>(
    R.layout.folder_details_fragment
) {

    override val screenName: String = "FolderDetailsFragment"

    override val viewModel by viewModel {
        val path = arguments?.getString(EXTRAS_FOLDER_PATH)
        Injector.folderVideoDetailsViewModel.also { viewModel ->
            path?.let { viewModel.loadSongsFromPath(it) }
        }
    }

    private val binding by viewBinding(FolderDetailsFragmentBinding::bind)

    private val adapter by lazy {
        LocalVideoAdapter(
            onClickTrack = {playVideo(it)},
            onSortClicked = {},
            onFilterClicked = {},
            showCountsAndSortButton = true,
            showFilter = false
        )
    }

    private fun playVideo(track: Track) {
        viewModel.onPlayVideo(track)
        val intent = Intent(activity, VideoPlayerActivity::class.java)
        intent.putExtra(VideoPlayerActivity.VIDEO_TYPE, track.type)
        intent.putExtra(VideoPlayerActivity.VIDEO_ID, track.id.toLong())
        intent.putExtra(VideoPlayerActivity.VIDEO_NAME, track.title)
        startActivity(intent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    private fun initViews() {
        val folderName = arguments?.getString(EXTRAS_FOLDER_NAME)
        folderName?.let { setupToolbar(binding.toolbarView.toolbar, it) }
        binding.localSongsRecyclerView.adapter = adapter
        observe(viewModel.localSongs, adapter::submitList)

        observe(PlaybackLiveData) { state ->
            if (state == PlayerConstants.PlayerState.PLAYING
                || state == PlayerConstants.PlayerState.BUFFERING
                || state == PlayerConstants.PlayerState.PAUSED
                || state == PlayerConstants.PlayerState.ENDED
            ) {
                viewModel.onPlaybackStateChanged()
            }
        }

        observe(DeviceInset) { inset ->
            binding.root.updatePadding(top = inset.top)
        }
    }

    companion object {
        const val EXTRAS_FOLDER_PATH = "extras.folder.path"
        const val EXTRAS_FOLDER_NAME = "extras.folder.name"

    }
}