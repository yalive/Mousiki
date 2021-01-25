package com.cas.musicplayer.domain.usecase.channel

import com.cas.musicplayer.data.repositories.PlaylistRepository
import com.mousiki.shared.domain.models.Playlist
import com.mousiki.shared.domain.result.Result
import com.mousiki.shared.domain.result.map
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-19.
 ***************************************
 */
class GetChannelPlaylistsUseCase @Inject constructor(
    private val repository: PlaylistRepository
) {
    suspend operator fun invoke(channelId: String): Result<List<Playlist>> {
        return repository.getPlaylists(channelId).map { playlists ->
            playlists.filter { it.itemCount > 0 }
        }
    }
}