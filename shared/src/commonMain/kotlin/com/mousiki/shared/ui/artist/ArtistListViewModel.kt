package com.mousiki.shared.ui.artist

import com.mousiki.shared.data.models.Artist
import com.mousiki.shared.domain.usecase.artist.GetAllArtistsUseCase
import com.mousiki.shared.ui.base.BaseViewModel
import com.mousiki.shared.ui.resource.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 **********************************
 * Created by Abdelhadi on 4/16/19.
 **********************************
 */
class ArtistListViewModel(
    val getAllArtists: GetAllArtistsUseCase
) : BaseViewModel() {

    private var _allArtists = listOf<Artist>()

    private val _artists = MutableStateFlow<Resource<List<Artist>>>(Resource.Loading)
    val artists: StateFlow<Resource<List<Artist>>> get() = _artists

    private var filter = ""

    init {
        loadAllArtists()
    }

    private fun loadAllArtists() = scope.launch {
        _allArtists = getAllArtists()
        filterArtists(filter)
    }

    fun filterArtists(filter: String) {
        this.filter = filter
        if (filter.isEmpty()) {
            _artists.value = Resource.Success(_allArtists)
            return
        }
        val artists = _allArtists.filter {
            it.name.toUpperCase().contains(filter.toUpperCase()) || filter == it.countryCode
        }
        _artists.value = Resource.Success(artists)
    }


    // For iOS
    inline fun observeArtists(
        crossinline onLoading: () -> Unit,
        crossinline onResult: (List<Artist>) -> Unit
    ) {
        scope.launch {
            artists.collect { resource ->
                when (resource) {
                    is Resource.Failure -> Unit
                    Resource.Loading -> onLoading()
                    is Resource.Success -> onResult(resource.data)
                }
            }
        }
    }
}