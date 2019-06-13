package com.secureappinc.musicplayer.ui.genres.detailgenre.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.secureappinc.musicplayer.repository.GenresRepository
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-12.
 ***************************************
 */

class GenrePlaylistsViewModelFactory @Inject constructor(val genresRepository: GenresRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return GenrePlaylistsViewModel(genresRepository) as T
    }
}