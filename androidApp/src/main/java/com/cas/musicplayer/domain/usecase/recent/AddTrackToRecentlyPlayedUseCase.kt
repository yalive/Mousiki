package com.cas.musicplayer.domain.usecase.recent

import com.mousiki.shared.data.repository.StatisticsRepository
import com.mousiki.shared.domain.models.MusicTrack
import com.cas.musicplayer.utils.bgContext
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-26.
 ***************************************
 */
class AddTrackToRecentlyPlayedUseCase @Inject constructor(
    private val statisticsRepository: StatisticsRepository
) {
    suspend operator fun invoke(track: MusicTrack) = withContext(bgContext) {
        statisticsRepository.addTrackToRecent(track)
    }
}