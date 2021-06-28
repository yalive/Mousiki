package com.mousiki.shared.domain.usecase.library

import com.mousiki.shared.data.repository.SongsRepository
import com.mousiki.shared.domain.models.YtbTrack

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-06.
 ***************************************
 */
class AddSongToFavouriteUseCase(
    private val songsRepository: SongsRepository
) {
    suspend operator fun invoke(track: YtbTrack) {
        songsRepository.addSongToFavourite(track)
    }
}