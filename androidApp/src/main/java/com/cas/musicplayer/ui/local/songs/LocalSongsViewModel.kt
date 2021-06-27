package com.cas.musicplayer.ui.local.songs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cas.musicplayer.ui.local.repository.LocalSongsRepository
import com.mousiki.shared.domain.models.Song
import com.mousiki.shared.ui.base.BaseViewModel

class LocalSongsViewModel(
    private val localSongsRepository: LocalSongsRepository
) : BaseViewModel() {

    private val _localSongs = MutableLiveData<List<Song>>()
    val localSongs: LiveData<List<Song>>
        get() = _localSongs

    init {
        loadAllSongs()
    }

    private fun loadAllSongs() {
        _localSongs.value = localSongsRepository.songs()
    }
}