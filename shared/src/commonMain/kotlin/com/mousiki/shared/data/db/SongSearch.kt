package com.mousiki.shared.data.db

import com.mousiki.shared.db.Songs_search_result
import com.mousiki.shared.domain.models.MusicTrack

typealias SongSearchEntity = Songs_search_result

fun Songs_search_result.toMusicTrack() = MusicTrack(
    youtubeId = youtube_id,
    title = title,
    duration = duration,
    artistName = title.split("-")[0]
)