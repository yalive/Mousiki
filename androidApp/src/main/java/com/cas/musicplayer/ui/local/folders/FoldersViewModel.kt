package com.cas.musicplayer.ui.local.folders

import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cas.musicplayer.ui.local.repository.FoldersRepository
import com.mousiki.shared.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class FoldersViewModel(
    private val foldersRepository: FoldersRepository,
) : BaseViewModel() {

    private val _folders = MutableLiveData<List<Folder>>()
    val folders: LiveData<List<Folder>>
        get() = _folders

    fun loadAllFolders(folderType: FolderType) = viewModelScope.launch {
        _folders.value = foldersRepository.getFolders(folderType)
    }
}

val Folder.shortPath: String
    get() {
        val pathSegments = path.toUri().pathSegments.toMutableList()
        pathSegments.removeAt(0)
        return "/${pathSegments.joinToString("/")}"
    }