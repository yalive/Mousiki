package com.cas.musicplayer.ui.local.artists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cas.musicplayer.ui.local.artists.model.LocalArtist
import com.cas.musicplayer.ui.local.repository.LocalArtistRepository
import com.mousiki.shared.ui.base.BaseViewModel

class LocalArtistsViewModel(
    private val localArtistRepository: LocalArtistRepository
) : BaseViewModel() {

    private val _localArtists = MutableLiveData<List<LocalArtist>>()
    val localArtists: LiveData<List<LocalArtist>>
        get() = _localArtists

    init {
        loadAllLocalArtists()
    }

    private fun loadAllLocalArtists() {
        _localArtists.value = localArtistRepository.artists()
    }
}