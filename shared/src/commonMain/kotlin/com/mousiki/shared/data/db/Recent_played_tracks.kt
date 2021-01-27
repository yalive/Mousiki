package com.mousiki.shared.data.db

import com.mousiki.shared.db.Recent_played_tracks
import com.mousiki.shared.domain.models.MusicTrack

fun Recent_played_tracks.toMusicTrack() = MusicTrack(
    youtubeId = youtube_id,
    title = title,
    duration = duration
)