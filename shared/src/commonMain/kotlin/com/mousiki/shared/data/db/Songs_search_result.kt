package com.mousiki.shared.data.db

import com.mousiki.shared.db.Songs_search_result
import com.mousiki.shared.domain.models.MusicTrack

fun Songs_search_result.toMusicTrack() = MusicTrack(
    youtubeId = youtube_id,
    title = title,
    duration = duration
)