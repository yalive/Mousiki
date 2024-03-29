package com.mousiki.shared.domain.usecase.library

import com.mousiki.shared.data.repository.StatisticsRepository
import com.mousiki.shared.domain.models.Track

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-01.
 ***************************************
 */
class GetHeavyTracksUseCase(
    private val statisticsRepository: StatisticsRepository
) {
    suspend operator fun invoke(max: Int = 10): List<Track> {
        return statisticsRepository.getHeavyList(max)
    }
}