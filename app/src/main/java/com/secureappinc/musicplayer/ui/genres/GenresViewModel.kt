package com.secureappinc.musicplayer.ui.genres

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.secureappinc.musicplayer.ui.home.models.GenreMusic

/**
 **********************************
 * Created by Abdelhadi on 4/26/19.
 **********************************
 */
class GenresViewModel : ViewModel() {

    val genres = MutableLiveData<List<GenreMusic>>()

    fun loadAllGenres() {
        genres.value = GenreMusic.allValues
    }
}