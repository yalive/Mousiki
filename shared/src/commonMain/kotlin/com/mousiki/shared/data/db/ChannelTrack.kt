package com.mousiki.shared.data.db

import com.mousiki.shared.db.Channel_tracks
import com.mousiki.shared.domain.models.MusicTrack

typealias ChannelTrackEntity = Channel_tracks

fun Channel_tracks.toMusicTrack() = MusicTrack(
    youtubeId = youtube_id,
    title = title,
    duration = duration,
    artistName = title.split("-")[0]
)