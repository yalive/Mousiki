package com.cas.musicplayer.ui.local.songs

import android.os.Bundle
import android.view.View
import com.cas.common.extensions.onClick
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.LocalSongsFragmentBinding
import com.cas.musicplayer.di.Injector
import com.cas.musicplayer.player.services.PlaybackLiveData
import com.cas.musicplayer.tmp.observe
import com.cas.musicplayer.ui.base.BaseFragment
import com.cas.musicplayer.ui.bottomsheet.SortByFragment
import com.cas.musicplayer.utils.PreferenceUtil
import com.cas.musicplayer.utils.viewBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants

class LocalSongsFragment : BaseFragment<LocalSongsViewModel>(
    R.layout.local_songs_fragment
), StoragePermissionDelegate by StoragePermissionDelegateImpl() {

    override val viewModel by viewModel { Injector.localSongsViewModel }

    private val binding by viewBinding(LocalSongsFragmentBinding::bind)

    private val adapter by lazy {
        LocalSongsAdapter(viewModel::onClickTrack)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.localSongsRecyclerView.adapter = adapter
        observe(viewModel.localSongs) {
            adapter.submitList(it)
            binding.songsCount.text = resources.getQuantityString(
                R.plurals.playlist_tracks_counts,
                it.size,
                it.size
            )
        }
        observe(PlaybackLiveData) { state ->
            if (state == PlayerConstants.PlayerState.PLAYING
                || state == PlayerConstants.PlayerState.BUFFERING
                || state == PlayerConstants.PlayerState.PAUSED
                || state == PlayerConstants.PlayerState.ENDED
            ) {
                viewModel.onPlaybackStateChanged()
            }
        }
        checkStoragePermission(binding.localSongsRecyclerView, binding.storagePermissionView) {
            viewModel.loadAllSongs()
        }

        binding.sortButton.onClick {
            saveAndSetOrder()
        }
    }

    private fun saveAndSetOrder() {
        SortByFragment.present(childFragmentManager) { currentSortOrder ->
            PreferenceUtil.songSortOrder = currentSortOrder
            viewModel.loadAllSongs()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) = onRequestPermissionsResultDelegate(requestCode, permissions, grantResults)
}