package com.cas.musicplayer.domain.model

import com.cas.common.result.Result

data class SearchTracksResult(
    val tracks: List<MusicTrack>,
    val token: String? = null,
    val key: String? = null
)

fun Result<SearchTracksResult>.hasData(): Boolean {
    return (this is Result.Success && data.tracks.isNotEmpty())
}