package com.mousiki.shared.data.db

import com.mousiki.shared.db.Channel_tracks
import com.mousiki.shared.domain.models.YtbTrack

typealias ChannelTrackEntity = Channel_tracks

fun Channel_tracks.toTrack() = YtbTrack(
    youtubeId = youtube_id,
    title = title,
    duration = duration,
    artistName = artist_name,
    artistId = channelId
)