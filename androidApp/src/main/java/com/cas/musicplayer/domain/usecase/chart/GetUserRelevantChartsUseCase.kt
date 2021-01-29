package com.cas.musicplayer.domain.usecase.chart

import com.mousiki.shared.data.repository.ChartsRepository
import com.mousiki.shared.domain.models.ChartModel
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-11.
 ***************************************
 */
class GetUserRelevantChartsUseCase @Inject constructor(
    private val repository: ChartsRepository
) {

    operator fun invoke(max: Int = 100): List<ChartModel> {
        return repository.getUserMostRelevantCharts().take(max)
    }
}