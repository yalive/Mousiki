package com.secureappinc.musicplayer.ui.artists.artistdetail.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.secureappinc.musicplayer.repository.ArtistsRepository
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-12.
 ***************************************
 */

class ArtistPlaylistsViewModelFactory @Inject constructor(val artistsRepository: ArtistsRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ArtistPlaylistsViewModel(artistsRepository) as T
    }
}