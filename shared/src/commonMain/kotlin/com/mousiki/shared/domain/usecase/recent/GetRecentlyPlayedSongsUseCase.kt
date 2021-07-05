package com.mousiki.shared.domain.usecase.recent

import com.mousiki.shared.data.repository.StatisticsRepository
import com.mousiki.shared.domain.models.Track

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-26.
 ***************************************
 */
class GetRecentlyPlayedSongsUseCase(
    private val statisticsRepository: StatisticsRepository
) {
    suspend operator fun invoke(): List<Track> {
        return statisticsRepository.getRecentlyPlayedTracks()
    }
}