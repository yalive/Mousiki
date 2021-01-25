package com.cas.musicplayer.domain.model

import com.cas.common.result.Result
import com.mousiki.shared.domain.models.SearchTracksResult

fun Result<SearchTracksResult>.hasData(): Boolean {
    return (this is Result.Success && data.tracks.isNotEmpty())
}
