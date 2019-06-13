package com.secureappinc.musicplayer.ui.artists.artistdetail.detailplaylist

import androidx.lifecycle.MutableLiveData
import com.secureappinc.musicplayer.base.BaseViewModel
import com.secureappinc.musicplayer.data.enteties.MusicTrack
import com.secureappinc.musicplayer.base.common.Resource
import com.secureappinc.musicplayer.repository.PlaylistRepository
import com.secureappinc.musicplayer.ui.home.uiScope
import kotlinx.coroutines.launch

/**
 **********************************
 * Created by Abdelhadi on 4/12/19.
 **********************************
 */
class PlaylistVideosViewModel(val playlistRepository: PlaylistRepository) : BaseViewModel() {

    val searchResultList = MutableLiveData<Resource<List<MusicTrack>>>()

    fun getPlaylistVideos(playlistId: String) = uiScope.launch(coroutineContext) {
        searchResultList.value = Resource.loading()
        val resource = playlistRepository.playlistVideos(playlistId)
        searchResultList.value = resource
    }
}