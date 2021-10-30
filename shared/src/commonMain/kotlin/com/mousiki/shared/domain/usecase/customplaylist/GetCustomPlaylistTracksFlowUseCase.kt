package com.mousiki.shared.domain.usecase.customplaylist

import com.mousiki.shared.data.repository.PlaylistsRepository
import com.mousiki.shared.domain.models.Track
import com.mousiki.shared.utils.CommonFlow
import com.mousiki.shared.utils.asCommonFlow
import kotlinx.coroutines.flow.Flow

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/4/20.
 ***************************************
 */
class GetCustomPlaylistTracksFlowUseCase(
    private val repository: PlaylistsRepository
) {

    operator fun invoke(playlistId: String): Flow<List<Track>> {
        return repository.getCustomPlaylistTracksFlow(playlistId.toLong())
    }

    fun streamOf(playlistId: String): CommonFlow<List<Track>> = invoke(playlistId).asCommonFlow()
}