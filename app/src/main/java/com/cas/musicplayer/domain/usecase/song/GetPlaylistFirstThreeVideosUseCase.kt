package com.cas.musicplayer.domain.usecase.song

import com.cas.common.result.Result
import com.cas.musicplayer.data.repositories.PlaylistRepository
import com.mousiki.shared.domain.models.MusicTrack
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-20.
 ***************************************
 */
class GetPlaylistFirstThreeVideosUseCase @Inject constructor(
    private val repository: PlaylistRepository
) {
    suspend operator fun invoke(playlistId: String): Result<List<MusicTrack>> {
        return repository.firstThreeVideo(playlistId)
    }
}