package com.secureappinc.musicplayer.ui.genres.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.secureappinc.musicplayer.ui.home.models.GenreMusic
import javax.inject.Inject

/**
 **********************************
 * Created by Abdelhadi on 4/26/19.
 **********************************
 */
class GenresViewModel @Inject constructor() : ViewModel() {

    val genres = MutableLiveData<List<GenreMusic>>()

    fun loadAllGenres() {
        genres.value = GenreMusic.allValues
    }
}