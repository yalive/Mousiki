package com.cas.musicplayer.ui.local.albums

import android.os.Bundle
import android.view.View
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.LocalAlbumsFragmentBinding
import com.cas.musicplayer.di.Injector
import com.cas.musicplayer.tmp.launchWhenViewResumed
import com.cas.musicplayer.tmp.observe
import com.cas.musicplayer.ui.base.BaseFragment
import com.cas.musicplayer.ui.local.songs.StoragePermissionDelegate
import com.cas.musicplayer.ui.local.songs.StoragePermissionDelegateImpl
import com.cas.musicplayer.utils.viewBinding

class LocalAlbumsFragment : BaseFragment<LocalAlbumsViewModel>(
    R.layout.local_albums_fragment
), StoragePermissionDelegate by StoragePermissionDelegateImpl() {

    companion object {
        fun newInstance() = LocalAlbumsFragment()
    }

    override val screenName: String = "LocalAlbumsFragment"
    override val viewModel by viewModel { Injector.localAlbumsViewModel }

    private val binding by viewBinding(LocalAlbumsFragmentBinding::bind)

    private val adapter by lazy {
        AlbumsAdapter()
    }

    init {
        launchWhenViewResumed {
            observe(viewModel.albums, adapter::submitList)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.albumsRecyclerView.adapter = adapter
        checkStoragePermission(binding.albumsRecyclerView, binding.storagePermissionView) {
            viewModel.loadAllAlbums()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) = onRequestPermissionsResultDelegate(requestCode, permissions, grantResults)

}