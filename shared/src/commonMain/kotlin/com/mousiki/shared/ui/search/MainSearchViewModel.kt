package com.mousiki.shared.ui.search

import com.mousiki.shared.domain.models.GenreMusic
import com.mousiki.shared.domain.usecase.genre.GetGenresUseCase
import com.mousiki.shared.ui.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 ***************************************
 * Created by Y.Abdelhadi on 3/26/20.
 ***************************************
 */
class MainSearchViewModel(
    private val getGenres: GetGenresUseCase
) : BaseViewModel() {

    private val _genres = MutableStateFlow<List<GenreMusic>?>(null)
    val genres: StateFlow<List<GenreMusic>?> = _genres

    init {
        prepareGenres()
    }

    private fun prepareGenres() = scope.launch {
        val chartList = getGenres()
        _genres.value = chartList
    }

    // For iOS
    inline fun observeGenres(
        crossinline onResult: (List<GenreMusic>) -> Unit
    ) {
        scope.launch {
            genres.collect { it?.let { onResult(it) } }
        }
    }
}