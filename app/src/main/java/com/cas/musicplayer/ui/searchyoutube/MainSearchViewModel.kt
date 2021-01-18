package com.cas.musicplayer.ui.searchyoutube

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cas.common.viewmodel.BaseViewModel
import com.cas.musicplayer.domain.model.GenreMusic
import com.cas.musicplayer.domain.usecase.genre.GetGenresUseCase
import com.cas.musicplayer.utils.uiCoroutine
import javax.inject.Inject

/**
 ***************************************
 * Created by Y.Abdelhadi on 3/26/20.
 ***************************************
 */
class MainSearchViewModel @Inject constructor(
    private val getGenres: GetGenresUseCase
) : BaseViewModel() {

    private val _genres = MutableLiveData<List<GenreMusic>>()
    val genres: LiveData<List<GenreMusic>> = _genres

    init {
        prepareGenres()
    }

    private fun prepareGenres() = uiCoroutine {
        val chartList = getGenres()
        _genres.value = chartList
    }
}