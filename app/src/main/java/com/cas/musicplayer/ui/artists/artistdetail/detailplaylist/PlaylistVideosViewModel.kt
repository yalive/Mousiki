package com.cas.musicplayer.ui.artists.artistdetail.detailplaylist

import androidx.lifecycle.MutableLiveData
import com.cas.musicplayer.base.BaseViewModel
import com.cas.musicplayer.base.common.ResourceOld
import com.cas.musicplayer.data.enteties.MusicTrack
import com.cas.musicplayer.repository.PlaylistRepository
import com.cas.musicplayer.utils.uiScope
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 **********************************
 * Created by Abdelhadi on 4/12/19.
 **********************************
 */
class PlaylistVideosViewModel @Inject constructor(val playlistRepository: PlaylistRepository) : BaseViewModel() {

    val searchResultList = MutableLiveData<ResourceOld<List<MusicTrack>>>()

    fun getPlaylistVideos(playlistId: String) = uiScope.launch(coroutineContext) {
        searchResultList.value = ResourceOld.loading()
        val resource = playlistRepository.playlistVideos(playlistId)
        searchResultList.value = resource
    }
}