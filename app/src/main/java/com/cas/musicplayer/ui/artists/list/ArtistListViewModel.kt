package com.cas.musicplayer.ui.artists.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cas.common.resource.Resource
import com.cas.common.resource.hasItems
import com.cas.common.resource.isLoading
import com.cas.common.result.Result
import com.cas.common.viewmodel.BaseViewModel
import com.cas.musicplayer.data.remote.models.Artist
import com.cas.musicplayer.domain.usecase.artist.GetArtistsFromAssetUseCase
import com.cas.musicplayer.domain.usecase.artist.GetArtistsThumbnailsUseCase
import com.cas.musicplayer.utils.uiCoroutine
import javax.inject.Inject

/**
 **********************************
 * Created by Abdelhadi on 4/16/19.
 **********************************
 */
class ArtistListViewModel @Inject constructor(
    val getArtistsFromAsset: GetArtistsFromAssetUseCase,
    val getArtistsThumbnails: GetArtistsThumbnailsUseCase
) : BaseViewModel() {

    private val _artists = MutableLiveData<Resource<List<Artist>>>()
    val artists: LiveData<Resource<List<Artist>>>
        get() = _artists


    fun loadAllArtists() = uiCoroutine {
        if (_artists.hasItems() || _artists.isLoading()) {
            return@uiCoroutine
        }
        _artists.value = Resource.Loading
        val artistList = getArtistsFromAsset()
        loadImages(artistList)
    }

    private suspend fun loadImages(artists: List<Artist>) {
        val numberOfTenGroups = artists.size / pageSize
        val rest = artists.size % pageSize
        for (i in 0 until numberOfTenGroups) {
            val subList = artists.subList(i * pageSize, (i + 1) * pageSize)
            loadArtists(subList.map { it.channelId })
        }
        // Load the rest
        val subList =
            artists.subList(numberOfTenGroups * pageSize, numberOfTenGroups * pageSize + rest)
        loadArtists(subList.map { it.channelId })
    }

    private suspend fun loadArtists(ids: List<String>) {
        val result = getArtistsThumbnails(ids)
        if (result is Result.Success) {
            appendArtists(result.data)
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

    companion object {
        private const val pageSize = 15
    }
}