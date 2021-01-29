package com.cas.musicplayer.domain.usecase.chart

import com.mousiki.shared.data.repository.ChartsRepository
import com.mousiki.shared.domain.models.ChartModel
import com.mousiki.shared.domain.models.MusicTrack
import javax.inject.Inject

/**
 *********************************************
 * Created by Abdelhadi on 2019-12-05.
 *
 *********************************************
 */
class LoadChartLastThreeTracksUseCase @Inject constructor(
    private val repository: ChartsRepository
) {

    suspend operator fun invoke(chart: ChartModel): List<MusicTrack> {
        return repository.loadChartLightTracks(chart)
    }
}