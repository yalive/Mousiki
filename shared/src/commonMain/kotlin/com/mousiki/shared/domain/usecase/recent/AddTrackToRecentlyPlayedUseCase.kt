package com.mousiki.shared.domain.usecase.recent

import com.mousiki.shared.data.repository.StatisticsRepository
import com.mousiki.shared.domain.models.MusicTrack

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-26.
 ***************************************
 */
class AddTrackToRecentlyPlayedUseCase(
    private val statisticsRepository: StatisticsRepository
) {
    suspend operator fun invoke(track: MusicTrack) {
        statisticsRepository.addTrackToRecent(track)
    }
}