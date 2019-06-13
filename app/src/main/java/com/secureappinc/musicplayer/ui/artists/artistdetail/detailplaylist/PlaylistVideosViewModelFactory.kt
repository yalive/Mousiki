package com.secureappinc.musicplayer.ui.artists.artistdetail.detailplaylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.secureappinc.musicplayer.repository.PlaylistRepository
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-12.
 ***************************************
 */

class PlaylistVideosViewModelFactory @Inject constructor(val playlistRepository: PlaylistRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PlaylistVideosViewModel(playlistRepository) as T
    }
}