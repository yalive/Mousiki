package com.mousiki.shared.domain.usecase.customplaylist

import com.mousiki.shared.data.repository.PlaylistsRepository
import com.mousiki.shared.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/4/20.
 ***************************************
 */
class GetLocalPlaylistsFlowUseCase(
    private val repository: PlaylistsRepository
) {

    suspend operator fun invoke(): Flow<List<Playlist>> {
        return repository.getPlaylistsFlow()
    }
}