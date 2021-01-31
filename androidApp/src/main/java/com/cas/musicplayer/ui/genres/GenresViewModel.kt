package com.cas.musicplayer.ui.genres

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cas.common.viewmodel.BaseViewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.utils.uiCoroutine
import com.mousiki.shared.domain.models.DisplayableItem
import com.mousiki.shared.domain.usecase.genre.GetGenresUseCase

/**
 **********************************
 * Created by Abdelhadi on 4/26/19.
 **********************************
 */
class GenresViewModel(
    private val getGenres: GetGenresUseCase
) : BaseViewModel() {

    private val _genres = MutableLiveData<List<DisplayableItem>>()
    val genres: LiveData<List<DisplayableItem>>
        get() = _genres

    init {
        loadAllGenres()
    }

    fun loadAllGenres() = uiCoroutine {
        val listGenres = getGenres()
        val musicGenres = listGenres.filter { !it.isMood }
        val moods = listGenres.filter { it.isMood }
        val displayedItems = mutableListOf<DisplayableItem>().apply {
            add(HeaderGenresItem(R.string.genre_music))
            addAll(musicGenres)
            add(HeaderGenresItem(R.string.genre_mood))
            addAll(moods)
        }
        _genres.value = displayedItems
    }

    fun isHeader(position: Int): Boolean {
        return _genres.value?.getOrNull(position) is HeaderGenresItem
    }
}