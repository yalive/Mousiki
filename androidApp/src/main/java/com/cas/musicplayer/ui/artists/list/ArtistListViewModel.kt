package com.cas.musicplayer.ui.artists.list

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cas.common.resource.Resource
import com.cas.common.resource.doOnSuccess
import com.cas.common.resource.hasItems
import com.cas.common.resource.isLoading
import com.cas.common.viewmodel.BaseViewModel
import com.cas.musicplayer.utils.uiCoroutine
import com.mousiki.shared.data.models.Artist
import com.mousiki.shared.domain.usecase.artist.GetAllArtistsUseCase

/**
 **********************************
 * Created by Abdelhadi on 4/16/19.
 **********************************
 */
class ArtistListViewModel(
    val getAllArtists: GetAllArtistsUseCase
) : BaseViewModel() {

    private val _artists = MutableLiveData<Resource<List<Artist>>>()

    private val _filteredArtists = MutableLiveData<Resource<List<Artist>>>()
    val filteredArtists: LiveData<Resource<List<Artist>>>
        get() = _filteredArtists

    private var filter = ""

    fun loadAllArtists() = uiCoroutine {
        if (_artists.hasItems() || _artists.isLoading()) {
            return@uiCoroutine
        }
        _artists.value = Resource.Loading
        val artistList = getAllArtists()
        _artists.value = Resource.Success(artistList)
        filterArtists(filter)
    }

    @SuppressLint("DefaultLocale")
    fun filterArtists(filter: String) {
        this.filter = filter
        _artists.value?.doOnSuccess { allArtists ->
            val filteredArtists = allArtists.filter {
                it.name.toUpperCase().contains(filter.toUpperCase()) || filter == it.countryCode
            }
            _filteredArtists.value = Resource.Success(filteredArtists)
        }
    }
}