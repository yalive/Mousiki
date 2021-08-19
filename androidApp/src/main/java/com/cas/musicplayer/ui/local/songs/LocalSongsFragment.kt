package com.cas.musicplayer.ui.local.songs

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.LocalSongsFragmentBinding
import com.cas.musicplayer.di.Injector
import com.cas.musicplayer.player.services.PlaybackLiveData
import com.cas.musicplayer.tmp.observe
import com.cas.musicplayer.ui.base.BaseFragment
import com.cas.musicplayer.ui.bottomsheet.SortByFragment
import com.cas.musicplayer.ui.local.songs.settings.LocalSongsSettingsFragment
import com.cas.musicplayer.utils.PreferenceUtil
import com.cas.musicplayer.utils.viewBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants

class LocalSongsFragment : BaseFragment<LocalSongsViewModel>(
    R.layout.local_songs_fragment
), StoragePermissionDelegate by StoragePermissionDelegateImpl() {

    override val screenName: String = "LocalSongsFragment"
    override val viewModel by viewModel { Injector.localSongsViewModel }

    private val binding by viewBinding(LocalSongsFragmentBinding::bind)

    private val adapter by lazy {
        LocalSongsAdapter(
            onClickTrack = { viewModel.onClickTrack(it) },
            onSortClicked = { saveAndSetOrder() },
            onFilterClicked = { showFilterScreen() },
            showCountsAndSortButton = true,
            showFilter = true
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.localSongsRecyclerView.adapter = adapter
        observe(PlaybackLiveData) { state ->
            if (state == PlayerConstants.PlayerState.PLAYING
                || state == PlayerConstants.PlayerState.BUFFERING
                || state == PlayerConstants.PlayerState.PAUSED
                || state == PlayerConstants.PlayerState.ENDED
            ) {
                viewModel.onPlaybackStateChanged()
            }
        }
        registerForActivityResult(
            this,
            binding.localSongsRecyclerView,
            binding.storagePermissionView
        )

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            observe(viewModel.localSongs) {
                adapter.submitList(it)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkStoragePermission {
            viewModel.loadAllSongs()
        }
    }

    private fun saveAndSetOrder() {
        SortByFragment.present(childFragmentManager) { currentSortOrder ->
            PreferenceUtil.songSortOrder = currentSortOrder
            viewModel.loadAllSongs()
        }
    }

    private fun showFilterScreen() {
        LocalSongsSettingsFragment.present(requireActivity() as AppCompatActivity) {
            viewModel.loadAllSongs()
        }
    }
}