package com.cas.musicplayer.ui.artists.list

import androidx.lifecycle.MutableLiveData
import com.cas.musicplayer.base.BaseViewModel
import com.cas.musicplayer.base.common.ResourceOld
import com.cas.musicplayer.base.common.Status
import com.cas.musicplayer.data.models.Artist
import com.cas.musicplayer.repository.ArtistsRepository
import com.cas.musicplayer.ui.home.ui.uiScope
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 **********************************
 * Created by Abdelhadi on 4/16/19.
 **********************************
 */
class ArtistListViewModel @Inject constructor(val repository: ArtistsRepository) : BaseViewModel() {

    var artistResources = MutableLiveData<ResourceOld<List<Artist>>>()

    private val pageSize = 15

    fun loadAllArtists() = uiScope.launch(coroutineContext) {
        if (artistResources.value != null && artistResources.value?.data != null && artistResources.value?.data!!.size > 0) {
            return@launch
        }
        artistResources.value = ResourceOld.loading()
        val artistList = repository.getArtistsFromFile()
        //artistResources.value = Resource.success(artistList)
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
        val subList = artists.subList(numberOfTenGroups * pageSize, numberOfTenGroups * pageSize + rest)
        loadArtists(subList.joinToString { it.channelId })
    }

    private suspend fun loadArtists(ids: String) {
        val resource = repository.getArtists(ids)
        if (resource.status == Status.SUCCESS) {
            appendArtists(resource.data!!)
        }
    }

    private fun appendArtists(artists: List<Artist>) {
        val resource = artistResources.value
        if (resource != null && resource.status == Status.SUCCESS) {
            artistResources.value = ResourceOld.success(resource.data!! + artists)
        } else {
            artistResources.value = ResourceOld.success(artists)
        }
    }
}