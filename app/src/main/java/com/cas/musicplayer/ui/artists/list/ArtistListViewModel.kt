package com.cas.musicplayer.ui.artists.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cas.musicplayer.base.BaseViewModel
import com.cas.musicplayer.base.common.Resource
import com.cas.musicplayer.base.common.Status
import com.cas.musicplayer.base.common.hasItems
import com.cas.musicplayer.base.common.isLoading
import com.cas.musicplayer.data.models.Artist
import com.cas.musicplayer.repository.ArtistsRepository
import com.cas.musicplayer.utils.uiCoroutine
import javax.inject.Inject

/**
 **********************************
 * Created by Abdelhadi on 4/16/19.
 **********************************
 */
class ArtistListViewModel @Inject constructor(
    val repository: ArtistsRepository
) : BaseViewModel() {

    private val _artists = MutableLiveData<Resource<List<Artist>>>()
    val artists: LiveData<Resource<List<Artist>>>
        get() = _artists

    private val pageSize = 15

    fun loadAllArtists() = uiCoroutine {
        if (_artists.hasItems() || _artists.isLoading()) {
            return@uiCoroutine
        }
        _artists.value = Resource.Loading
        val artistList = repository.getArtistsFromFile()
        _artists.value = Resource.Success(artistList)
        loadImages(artistList)
    }

    private suspend fun loadImages(artists: List<Artist>) {
        val numberOfTenGroups = artists.size / pageSize
        val rest = artists.size % pageSize
        for (i in 0 until numberOfTenGroups) {
            val subList = artists.subList(i * pageSize, (i + 1) * pageSize)
            loadArtists(subList.joinToString { it.channelId })
        }
        // Load the rest
        val subList =
            artists.subList(numberOfTenGroups * pageSize, numberOfTenGroups * pageSize + rest)
        loadArtists(subList.joinToString { it.channelId })
    }

    private suspend fun loadArtists(ids: String) {
        val resource = repository.getArtists(ids)
        if (resource.status == Status.SUCCESS) {
            appendArtists(resource.data!!)
        }
    }

    private fun appendArtists(artists: List<Artist>) {
        val resource = _artists.value
        if (resource != null && resource is Resource.Success) {
            _artists.value = Resource.Success(resource.data + artists)
        } else {
            _artists.value = Resource.Success(artists)
        }
    }
}