package com.mousiki.shared.domain.usecase.channel

import com.mousiki.shared.data.repository.PlaylistRepository
import com.mousiki.shared.domain.models.Playlist
import com.mousiki.shared.domain.result.Result
import com.mousiki.shared.domain.result.map

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-19.
 ***************************************
 */
class GetChannelPlaylistsUseCase(
    private val repository: PlaylistRepository
) {
    suspend operator fun invoke(channelId: String): Result<List<Playlist>> {
        return repository.getPlaylists(channelId).map { playlists ->
            playlists.filter { it.itemCount > 0 }
        }
    }
}