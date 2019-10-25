package com.cas.musicplayer.ui.artists.artistdetail.detailplaylist

import androidx.lifecycle.MutableLiveData
import com.cas.musicplayer.base.BaseViewModel
import com.cas.musicplayer.base.common.Resource
import com.cas.musicplayer.data.enteties.MusicTrack
import com.cas.musicplayer.repository.PlaylistRepository
import com.cas.musicplayer.ui.home.uiScope
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 **********************************
 * Created by Abdelhadi on 4/12/19.
 **********************************
 */
class PlaylistVideosViewModel @Inject constructor(val playlistRepository: PlaylistRepository) : BaseViewModel() {

    val searchResultList = MutableLiveData<Resource<List<MusicTrack>>>()

    fun getPlaylistVideos(playlistId: String) = uiScope.launch(coroutineContext) {
        searchResultList.value = Resource.loading()
        val resource = playlistRepository.playlistVideos(playlistId)
        searchResultList.value = resource
    }
}