package com.cas.musicplayer.ui.genres.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cas.musicplayer.base.BaseViewModel
import com.cas.musicplayer.ui.home.domain.model.GenreMusic
import com.cas.musicplayer.ui.home.domain.usecase.GetGenresUseCase
import com.cas.musicplayer.utils.uiCoroutine
import javax.inject.Inject

/**
 **********************************
 * Created by Abdelhadi on 4/26/19.
 **********************************
 */
class GenresViewModel @Inject constructor(
    private val getGenres: GetGenresUseCase
) : BaseViewModel() {

    private val _genres = MutableLiveData<List<GenreMusic>>()
    val genres: LiveData<List<GenreMusic>>
        get() = _genres

    fun loadAllGenres() = uiCoroutine {
        val listGenres = getGenres()
        _genres.value = listGenres
    }
}