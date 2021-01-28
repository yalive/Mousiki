package com.cas.musicplayer.domain.usecase.library

import com.cas.musicplayer.data.repositories.SongsRepository
import com.mousiki.shared.domain.models.MusicTrack
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-06.
 ***************************************
 */
class GetFavouriteTracksFlowUseCase @Inject constructor(
    private val songsRepository: SongsRepository
) {
    suspend operator fun invoke(max: Int = 10): Flow<List<MusicTrack>> {
        return songsRepository.getFavouriteSongsFlow(max)
    }
}