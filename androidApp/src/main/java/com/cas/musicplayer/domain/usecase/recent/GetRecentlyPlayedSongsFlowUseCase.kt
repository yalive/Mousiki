package com.cas.musicplayer.domain.usecase.recent

import com.mousiki.shared.data.repository.StatisticsRepository
import com.mousiki.shared.domain.models.MusicTrack
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-26.
 ***************************************
 */
class GetRecentlyPlayedSongsFlowUseCase @Inject constructor(
    private val statisticsRepository: StatisticsRepository
) {
    suspend operator fun invoke(max: Int = 10): Flow<List<MusicTrack>> {
        return statisticsRepository.getRecentlyPlayedTracksFlow(max)
    }
}