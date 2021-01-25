package com.cas.musicplayer.domain.usecase.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.cas.musicplayer.data.repositories.StatisticsRepository
import com.mousiki.shared.domain.models.MusicTrack
import com.cas.musicplayer.utils.bgContext
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-01.
 ***************************************
 */
class GetHeavyTracksUseCase @Inject constructor(
    private val statisticsRepository: StatisticsRepository
) {
    suspend operator fun invoke(max: Int = 10): LiveData<List<MusicTrack>> =
        withContext(bgContext) {
            return@withContext Transformations.map(statisticsRepository.getHeavyListLive(max)) { tracks ->
                if (tracks.size < 3) {
                    return@map emptyList<MusicTrack>()
                }
                return@map tracks
            }
        }
}