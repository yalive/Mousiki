package com.cas.musicplayer.ui.bottomsheet

import android.app.RecoverableSecurityException
import android.content.IntentSender
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cas.musicplayer.ui.local.folders.FolderType
import com.cas.musicplayer.ui.local.repository.LocalSongsRepository
import com.cas.musicplayer.ui.local.repository.LocalVideosRepository
import com.mousiki.shared.domain.models.Song
import com.mousiki.shared.ui.base.BaseViewModel
import kotlinx.coroutines.launch

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-06.
 ***************************************
 */
class SongInfoViewModel(
    private val localSongsRepository: LocalSongsRepository,
    private val localVideoRepository: LocalVideosRepository
) : BaseViewModel() {

    private val _song = MutableLiveData<Song>()
    val song: LiveData<Song> get() = _song
    var folderType: FolderType = FolderType.SONG


    fun initTrack(songId: Long) {
        viewModelScope.launch {
            _song.value =
                if (folderType == FolderType.SONG) localSongsRepository.song(songId) else localVideoRepository.video(
                    songId
                )
        }
    }

    fun updateTrack(
        name: String,
        artist: String,
        album: String,
        composer: String,
        onNeedPermission: (IntentSender) -> Unit
    ) {
        if (folderType == FolderType.SONG) {
            updateSong(name,artist, album, composer, onNeedPermission)
        } else {
            updateVideo(name,artist, album, composer, onNeedPermission)
        }
    }


    private fun updateSong(
        name: String,
        artist: String,
        album: String,
        composer: String,
        onNeedPermission: (IntentSender) -> Unit
    ) {
        viewModelScope.launch {
            if (song.value != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    try {
                        localSongsRepository.updateSong(
                            _song.value!!.id,
                            name,
                            artist,
                            album,
                            composer
                        )?.let { _song.value = it }
                    } catch (securityException: Exception) {
                        val recoverableSecurityException = securityException as?
                                RecoverableSecurityException
                            ?: throw RuntimeException(securityException.message, securityException)
                        val intentSender =
                            recoverableSecurityException.userAction.actionIntent.intentSender
                        onNeedPermission(intentSender)
                    }
                } else {
                    localSongsRepository.updateSong(
                        _song.value!!.id,
                        name,
                        artist,
                        album,
                        composer
                    )?.let { _song.value = it }
                }
            }
        }
    }

    private fun updateVideo(
        name: String,
        artist: String,
        album: String,
        composer: String,
        onNeedPermission: (IntentSender) -> Unit
    ) {
        viewModelScope.launch {
            if (song.value != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    try {
                        localVideoRepository.updateSong(
                            _song.value!!.id,
                            name,
                            artist,
                            album,
                            composer
                        )?.let { _song.value = it }
                    } catch (securityException: Exception) {
                        val recoverableSecurityException = securityException as?
                                RecoverableSecurityException
                            ?: throw RuntimeException(securityException.message, securityException)
                        val intentSender =
                            recoverableSecurityException.userAction.actionIntent.intentSender
                        onNeedPermission(intentSender)
                    }
                } else {
                    localVideoRepository.updateSong(
                        _song.value!!.id,
                        name,
                        artist,
                        album,
                        composer
                    )?.let { _song.value = it }
                }
            }
        }
    }

}