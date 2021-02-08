package com.cas.musicplayer.ui.searchyoutube

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mousiki.shared.ui.base.BaseViewModel
import com.mousiki.shared.domain.models.GenreMusic
import com.mousiki.shared.domain.usecase.genre.GetGenresUseCase
import kotlinx.coroutines.launch

/**
 ***************************************
 * Created by Y.Abdelhadi on 3/26/20.
 ***************************************
 */
class MainSearchViewModel(
    private val getGenres: GetGenresUseCase
) : BaseViewModel() {

    private val _genres = MutableLiveData<List<GenreMusic>>()
    val genres: LiveData<List<GenreMusic>> = _genres

    init {
        prepareGenres()
    }

    private fun prepareGenres() = viewModelScope.launch {
        val chartList = getGenres()
        _genres.value = chartList
    }
}