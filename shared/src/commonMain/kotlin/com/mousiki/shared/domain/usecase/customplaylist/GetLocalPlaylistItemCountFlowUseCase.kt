package com.mousiki.shared.domain.usecase.customplaylist

import com.mousiki.shared.data.repository.PlaylistsRepository
import com.mousiki.shared.domain.models.Playlist
import com.mousiki.shared.utils.CommonFlow
import com.mousiki.shared.utils.asCommonFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/4/20.
 ***************************************
 */
class GetLocalPlaylistItemCountFlowUseCase(
    private val repository: PlaylistsRepository
) {

    operator fun invoke(playlist: Playlist): Flow<Int> {
        return repository.getPlaylistItemsCount(playlist).map { it.toInt() }
    }

    fun streamOf(playlist: Playlist): CommonFlow<Int> = invoke(playlist).asCommonFlow()
}