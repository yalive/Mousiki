package com.cas.musicplayer.ui.local.folders

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.FoldersFragmentBinding
import com.cas.musicplayer.di.Injector
import com.cas.musicplayer.tmp.observe
import com.cas.musicplayer.ui.base.BaseFragment
import com.cas.musicplayer.ui.local.songs.StoragePermissionDelegate
import com.cas.musicplayer.ui.local.songs.StoragePermissionDelegateImpl
import com.cas.musicplayer.utils.viewBinding

class FoldersFragment : BaseFragment<FoldersViewModel>(
    R.layout.folders_fragment
), StoragePermissionDelegate by StoragePermissionDelegateImpl() {

    companion object {
        fun newInstance() = FoldersFragment()
    }

    override val screenName: String = "FoldersFragment"
    override val viewModel by viewModel { Injector.foldersViewModel }

    private val binding by viewBinding(FoldersFragmentBinding::bind)

    private val adapter by lazy {
        FoldersAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.folderRecyclerView.adapter = adapter
        registerForActivityResult(this, binding.folderRecyclerView, binding.storagePermissionView)
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            observe(viewModel.folders, adapter::submitList)
        }
    }

    override fun onResume() {
        super.onResume()
        checkStoragePermission() {
            viewModel.loadAllFolders()
        }
    }

}