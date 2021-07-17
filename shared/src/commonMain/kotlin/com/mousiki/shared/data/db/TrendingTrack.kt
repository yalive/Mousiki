package com.mousiki.shared.data.db

import com.mousiki.shared.db.Trending_tracks
import com.mousiki.shared.domain.models.YtbTrack

typealias TrendingTrackEntity = Trending_tracks

fun Trending_tracks.toTrack() = YtbTrack(
    youtubeId = youtube_id,
    title = title,
    duration = duration,
    artistName = artist_name,
    artistId = artist_id
)