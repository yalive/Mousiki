package com.mousiki.shared.domain.usecase.customplaylist

import com.mousiki.shared.data.repository.PlaylistsRepository
import com.mousiki.shared.domain.models.Playlist
import com.mousiki.shared.utils.CommonFlow
import com.mousiki.shared.utils.asCommonFlow
import kotlinx.coroutines.flow.Flow

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/4/20.
 ***************************************
 */
class GetLocalPlaylistsFlowUseCase(
    private val repository: PlaylistsRepository
) {

    operator fun invoke(): Flow<List<Playlist>> {
        return repository.getPlaylistsFlow()
    }

    fun stream(): CommonFlow<List<Playlist>> = invoke().asCommonFlow()
}