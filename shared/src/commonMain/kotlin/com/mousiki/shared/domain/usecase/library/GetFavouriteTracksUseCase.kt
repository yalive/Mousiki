package com.mousiki.shared.domain.usecase.library

import com.mousiki.shared.data.repository.SongsRepository
import com.mousiki.shared.domain.models.MusicTrack

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-06.
 ***************************************
 */
class GetFavouriteTracksUseCase(
    private val songsRepository: SongsRepository
) {
    suspend operator fun invoke(max: Int = 10): List<MusicTrack> {
        return songsRepository.getFavouriteSongs(max)
    }
}