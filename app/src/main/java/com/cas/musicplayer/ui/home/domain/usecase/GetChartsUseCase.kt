package com.cas.musicplayer.ui.home.domain.usecase

import com.cas.musicplayer.ui.home.domain.model.ChartModel
import com.cas.musicplayer.ui.home.domain.repository.HomeRepository
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-11.
 ***************************************
 */
class GetChartsUseCase @Inject constructor(
    private val repository: HomeRepository
) {

    suspend operator fun invoke(): List<ChartModel> {
        return repository.loadCharts().take(6).shuffled()
    }
}