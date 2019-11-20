package com.cas.musicplayer.domain.usecase.chart

import com.cas.musicplayer.data.repositories.ChartsRepository
import com.cas.musicplayer.domain.model.ChartModel
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-11.
 ***************************************
 */
class GetChartsUseCase @Inject constructor(
    private val repository: ChartsRepository
) {

    suspend operator fun invoke(max: Int = 100): List<ChartModel> {
        return repository.loadCharts().take(max)
    }
}