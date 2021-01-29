package com.cas.musicplayer.domain.usecase.library

import com.mousiki.shared.data.repository.SongsRepository
import com.mousiki.shared.domain.models.MusicTrack
import com.cas.musicplayer.utils.bgContext
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-06.
 ***************************************
 */
class GetFavouriteTracksUseCase @Inject constructor(
    private val songsRepository: SongsRepository
) {
    suspend operator fun invoke(max: Int = 10): List<MusicTrack> = withContext(bgContext) {
        return@withContext songsRepository.getFavouriteSongs(max)
    }
}