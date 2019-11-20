package com.cas.musicplayer.ui.commondomain

import com.cas.musicplayer.data.enteties.MusicTrack
import com.cas.musicplayer.net.Result
import com.cas.musicplayer.repository.PlaylistRepository
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-19.
 ***************************************
 */
class GetPlaylistVideosUseCase @Inject constructor(
    private val repository: PlaylistRepository
) {

    suspend operator fun invoke(playlistId: String): Result<List<MusicTrack>> {
        return repository.playlistVideos(playlistId)
    }
}