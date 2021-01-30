package com.mousiki.shared.domain.usecase.library

import com.mousiki.shared.data.repository.SongsRepository

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-06.
 ***************************************
 */
class RemoveSongFromFavouriteListUseCase(
    private val songsRepository: SongsRepository
) {
    suspend operator fun invoke(trackId: String) {
        songsRepository.removeSongFromFavourite(trackId)
    }
}