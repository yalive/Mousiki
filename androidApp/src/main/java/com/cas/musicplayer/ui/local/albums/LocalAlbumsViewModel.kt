package com.cas.musicplayer.ui.local.albums

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cas.musicplayer.ui.local.repository.AlbumRepository
import com.mousiki.shared.domain.models.Album
import com.mousiki.shared.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class LocalAlbumsViewModel(
    private val albumRepository: AlbumRepository
) : BaseViewModel() {

    private val _albums = MutableLiveData<List<Album>>()
    val albums: LiveData<List<Album>>
        get() = _albums

    init {
        loadAllAlbums()
    }

    fun loadAllAlbums() = viewModelScope.launch {
        _albums.value = albumRepository.albums()
    }
}