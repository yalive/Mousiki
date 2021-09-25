package com.cas.musicplayer.ui.local.artists

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.LocalArtistsFragmentBinding
import com.cas.musicplayer.di.Injector
import com.cas.musicplayer.tmp.observe
import com.cas.musicplayer.ui.base.BaseFragment
import com.cas.musicplayer.ui.local.StoragePermissionDelegate
import com.cas.musicplayer.ui.local.StoragePermissionDelegateImpl
import com.cas.musicplayer.utils.viewBinding

class LocalArtistsFragment : BaseFragment<LocalArtistsViewModel>(
    R.layout.local_artists_fragment
), StoragePermissionDelegate by StoragePermissionDelegateImpl() {

    companion object {
        fun newInstance() = LocalArtistsFragment()
    }

    override val screenName: String = "LocalArtistsFragment"
    override val viewModel by viewModel { Injector.localArtistsViewModel }

    private val binding by viewBinding(LocalArtistsFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = LocalArtistsAdapter()
        binding.localArtistsRecyclerView.adapter = adapter
        registerForActivityResult(
            this,
            binding.localArtistsRecyclerView,
            binding.storagePermissionView
        )
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            observe(viewModel.localArtists, adapter::submitList)
        }
    }

    override fun onResume() {
        super.onResume()
        checkStoragePermission(
        ) {
            viewModel.loadAllLocalArtists()
        }
    }

}