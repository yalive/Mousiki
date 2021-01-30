package com.mousiki.shared.domain.usecase.chart

import com.mousiki.shared.data.repository.ChartsRepository
import com.mousiki.shared.domain.models.ChartModel

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-11.
 ***************************************
 */
class GetUserRelevantChartsUseCase(
    private val repository: ChartsRepository
) {

    operator fun invoke(max: Int = 100): List<ChartModel> {
        return repository.getUserMostRelevantCharts().take(max)
    }
}