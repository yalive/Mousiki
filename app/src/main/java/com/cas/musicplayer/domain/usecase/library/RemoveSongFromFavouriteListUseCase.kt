package com.cas.musicplayer.domain.usecase.library

import com.cas.musicplayer.data.repositories.SongsRepository
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.utils.bgContext
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-06.
 ***************************************
 */
class RemoveSongFromFavouriteListUseCase @Inject constructor(
    private val songsRepository: SongsRepository
) {
    suspend operator fun invoke(track: MusicTrack) = withContext(bgContext) {
        songsRepository.removeSongFromFavourite(track)
    }
}