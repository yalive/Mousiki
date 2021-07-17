package com.mousiki.shared.data.db

import com.mousiki.shared.db.Songs_search_result
import com.mousiki.shared.domain.models.YtbTrack

typealias SongSearchEntity = Songs_search_result

fun Songs_search_result.toTrack() = YtbTrack(
    youtubeId = youtube_id,
    title = title,
    duration = duration,
    artistName = artist_name,
    artistId = artist_id
)