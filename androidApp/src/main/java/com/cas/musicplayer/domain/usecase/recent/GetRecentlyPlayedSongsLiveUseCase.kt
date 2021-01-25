package com.cas.musicplayer.domain.usecase.recent

import androidx.lifecycle.LiveData
import com.cas.musicplayer.data.repositories.StatisticsRepository
import com.mousiki.shared.domain.models.MusicTrack
import com.cas.musicplayer.utils.bgContext
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-26.
 ***************************************
 */
class GetRecentlyPlayedSongsLiveUseCase @Inject constructor(
    private val statisticsRepository: StatisticsRepository
) {
    suspend operator fun invoke(max: Int = 10): LiveData<List<MusicTrack>> =
        withContext(bgContext) {
            return@withContext statisticsRepository.getRecentlyPlayedTracksLive(max)
        }
}