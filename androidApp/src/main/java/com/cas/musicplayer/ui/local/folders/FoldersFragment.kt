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
import com.cas.musicplayer.ui.local.folders.options.FolderOption
import com.cas.musicplayer.ui.local.StoragePermissionDelegate
import com.cas.musicplayer.ui.local.StoragePermissionDelegateImpl
import com.cas.musicplayer.utils.toast
import com.cas.musicplayer.utils.viewBinding

class FoldersFragment : BaseFragment<FoldersViewModel>(
    R.layout.folders_fragment
), StoragePermissionDelegate by StoragePermissionDelegateImpl() {

    override val screenName: String = "FoldersFragment"
    override val viewModel by viewModel { Injector.foldersViewModel }

    private val binding by viewBinding(FoldersFragmentBinding::bind)

    private val adapter by lazy {
        FoldersAdapter(::onFolderOption)
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
            viewModel.loadAllFolders(folderType)
        }
    }

    private fun onFolderOption(folder: Folder, option: FolderOption) {
        when (option) {
            FolderOption.Hidden -> {
                context?.toast(getString(R.string.folder_hidden_message, folder.name))
                viewModel.loadAllFolders(folderType)
            }
            else -> Unit
        }
    }

    private var folderType = FolderType.SONG

    companion object {
        fun newInstance(folderType: FolderType) =
            FoldersFragment().also { it.folderType = folderType }
    }
}