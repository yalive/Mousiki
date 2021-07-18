package com.cas.musicplayer.ui.local.folders

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cas.musicplayer.ui.local.repository.FoldersRepository
import com.mousiki.shared.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class FoldersViewModel(
    private val foldersRepository: FoldersRepository
) : BaseViewModel() {

    private val _folders = MutableLiveData<List<Folder>>()
    val folders: LiveData<List<Folder>>
        get() = _folders

    init {
        loadAllFolders()
    }

    fun loadAllFolders() = viewModelScope.launch {
        val folders1 = foldersRepository.getFolders()
        _folders.value = folders1
    }
}