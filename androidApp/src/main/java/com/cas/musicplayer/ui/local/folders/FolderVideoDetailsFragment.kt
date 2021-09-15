package com.cas.musicplayer.ui.local.folders

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.updatePadding
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.FolderDetailsFragmentBinding
import com.cas.musicplayer.di.Injector
import com.cas.musicplayer.tmp.observe
import com.cas.musicplayer.ui.base.BaseFragment
import com.cas.musicplayer.ui.base.setupToolbar
import com.cas.musicplayer.ui.local.videos.LocalVideoAdapter
import com.cas.musicplayer.ui.local.videos.player.VideoPlayerActivity
import com.cas.musicplayer.ui.local.videos.player.VideoQueueType
import com.cas.musicplayer.utils.DeviceInset
import com.cas.musicplayer.utils.viewBinding
import com.mousiki.shared.domain.models.Track

class FolderVideoDetailsFragment : BaseFragment<FolderVideoDetailsViewModel>(
    R.layout.folder_details_fragment
) {

    override val screenName: String = "FolderDetailsFragment"
    private val folder by lazy { requireArguments().getParcelable<Folder>(EXTRAS_FOLDER)!! }
    override val viewModel by viewModel {
        Injector.folderVideoDetailsViewModel.also { viewModel ->
            viewModel.loadSongsFromPath(folder.path)
        }
    }

    private val binding by viewBinding(FolderDetailsFragmentBinding::bind)

    private val adapter by lazy {
        LocalVideoAdapter(
            onClickTrack = { playVideo(it) },
            onSortClicked = {},
            onFilterClicked = {},
            showCountsAndSortButton = true,
            showFilter = false
        )
    }

    private fun playVideo(track: Track) {
        viewModel.onPlayVideo(track)
        val intent = Intent(activity, VideoPlayerActivity::class.java)
        intent.putExtra(VideoPlayerActivity.VIDEO, track)
        intent.putExtra(VideoPlayerActivity.QUEUE_TYPE, VideoQueueType.FolderLocation(folder))
        startActivity(intent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    private fun initViews() {
        setupToolbar(binding.toolbarView.toolbar, folder.name)
        binding.localSongsRecyclerView.adapter = adapter
        observe(viewModel.localSongs, adapter::submitList)

        observe(DeviceInset) { inset ->
            binding.root.updatePadding(top = inset.top)
        }
    }

    companion object {
        const val EXTRAS_FOLDER = "extras.folder"
    }
}