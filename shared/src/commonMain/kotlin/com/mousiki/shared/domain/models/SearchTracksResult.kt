package com.mousiki.shared.domain.models

import com.mousiki.shared.domain.result.Result

data class SearchTracksResult(
    val tracks: List<YtbTrack>,
    val token: String? = null,
    val key: String? = null
)

fun Result<SearchTracksResult>.hasData(): Boolean {
    return (this is Result.Success && data.tracks.isNotEmpty())
}
