package com.mousiki.shared.domain.usecase.library

import com.mousiki.shared.data.repository.SongsRepository
import com.mousiki.shared.data.repository.StatisticsRepository

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-06.
 ***************************************
 */
class RemoveSongFromRecentlyPlayedUseCase(
    private val statisticsRepository: StatisticsRepository
) {
    suspend operator fun invoke(trackId: String) {
        statisticsRepository.removeTrackFromRecent(trackId)
    }
}