package com.mousiki.shared.data.db

import com.mousiki.shared.db.Trending_tracks
import com.mousiki.shared.domain.models.MusicTrack

typealias TrendingTrackEntity = Trending_tracks

fun Trending_tracks.toMusicTrack() = MusicTrack(
    youtubeId = youtube_id,
    title = title,
    duration = duration,
    artistName = title.split("-")[0]
)