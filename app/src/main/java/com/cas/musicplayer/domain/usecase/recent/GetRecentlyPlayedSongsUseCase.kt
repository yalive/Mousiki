package com.cas.musicplayer.domain.usecase.recent

import com.cas.musicplayer.data.repositories.StatisticsRepository
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.utils.bgContext
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-26.
 ***************************************
 */
class GetRecentlyPlayedSongsUseCase @Inject constructor(
    private val statisticsRepository: StatisticsRepository
) {
    suspend operator fun invoke(): List<MusicTrack> = withContext(bgContext) {
        return@withContext statisticsRepository.getRecentlyPlayedTracks()
    }
}