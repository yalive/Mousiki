package com.cas.musicplayer.ui.local.videos

import android.content.ContentUris
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cas.musicplayer.MusicApp
import com.cas.musicplayer.ui.local.repository.LocalVideosRepository
import com.cas.musicplayer.ui.local.repository.filterNotHidden
import com.cas.musicplayer.utils.SongsUtil
import com.cas.musicplayer.utils.Utils
import com.mousiki.shared.domain.models.*
import com.mousiki.shared.domain.usecase.recent.AddVideoToRecentlyPlayedUseCase
import com.mousiki.shared.player.PlaySongDelegate
import com.mousiki.shared.player.updateCurrentPlaying
import com.mousiki.shared.ui.base.BaseViewModel
import com.mousiki.shared.ui.resource.Resource
import com.mousiki.shared.ui.resource.doOnSuccess
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.onStart
import java.io.File

class LocalVideoViewModel(
    private val localSongsRepository: LocalVideosRepository,
    private val addVideoToRecentlyPlayed: AddVideoToRecentlyPlayedUseCase,
    private val playSongsDelegate: PlaySongDelegate,
) : BaseViewModel(), PlaySongDelegate by playSongsDelegate {

    private val _localSongs = MutableLiveData<Resource<List<DisplayableItem>>>()
    val localSongs: LiveData<Resource<List<DisplayableItem>>>
        get() = _localSongs

    fun loadAllVideos() = viewModelScope.launch(Dispatchers.IO) {
        _localSongs.postValue(Resource.Loading)
        val songs = localSongsRepository.videos().filterNotHidden()
        val songsItems = songs.map {
            LocalSong(it).toDisplayedVideoItem()
        }

        val displayedItems = mutableListOf<DisplayableItem>().apply {
            add(HeaderVideosActionsItem(songsItems.size))
            addAll(songsItems)
        }
        _localSongs.postValue(Resource.Success(updateCurrentPlaying(displayedItems)))
    }

    fun onPlayVideo(track: Track) = viewModelScope.launch {
        addVideoToRecentlyPlayed(track)
    }
}