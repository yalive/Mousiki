package com.cas.musicplayer.ui.artists.artistdetail.videos

import androidx.lifecycle.MutableLiveData
import com.cas.musicplayer.base.BaseViewModel
import com.cas.musicplayer.base.common.Resource
import com.cas.musicplayer.data.enteties.MusicTrack
import com.cas.musicplayer.repository.ArtistsRepository
import com.cas.musicplayer.ui.home.uiScope
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 **********************************
 * Created by Abdelhadi on 4/12/19.
 **********************************
 */
class ArtistVideosViewModel @Inject constructor(val repository: ArtistsRepository) : BaseViewModel() {

    val tracks = MutableLiveData<Resource<List<MusicTrack>>>()

    fun loadArtistTracks(channelId: String) = uiScope.launch(coroutineContext) {
        tracks.value = Resource.loading()
        val resource = repository.getArtistTracks(channelId)
        tracks.value = resource
    }
}