package com.mousiki.shared.domain.usecase.customplaylist

import com.mousiki.shared.data.repository.PlaylistsRepository
import com.mousiki.shared.domain.models.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/4/20.
 ***************************************
 */
class GetLocalPlaylistItemCountUseCase(
    private val repository: PlaylistsRepository
) {

    operator fun invoke(playlist: Playlist): Flow<Int> {
        return repository.getPlaylistItemsCount(playlist).map { it.toInt() }
    }
}