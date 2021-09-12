package com.cas.musicplayer.ui.bottomsheet

import android.app.RecoverableSecurityException
import android.content.IntentSender
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cas.musicplayer.ui.local.repository.LocalSongsRepository
import com.mousiki.shared.domain.models.Song
import com.mousiki.shared.ui.base.BaseViewModel
import kotlinx.coroutines.launch

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-06.
 ***************************************
 */
class TrackInfoViewModel(
    private val localSongsRepository: LocalSongsRepository
) : BaseViewModel() {

    private val _song = MutableLiveData<Song>()
    val song: LiveData<Song> get() = _song


    fun initSong(songId: Long) {
        viewModelScope.launch {
            _song.value = localSongsRepository.song(songId)
        }
    }

    fun updateSong(
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

}