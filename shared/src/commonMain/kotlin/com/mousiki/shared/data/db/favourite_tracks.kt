package com.mousiki.shared.data.db

import com.mousiki.shared.db.Favourite_tracks
import com.mousiki.shared.domain.models.MusicTrack

fun Favourite_tracks.toMusicTrack() = MusicTrack(
    youtubeId = youtube_id,
    title = title,
    duration = duration
)
