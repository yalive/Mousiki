package com.mousiki.shared.data.db

import com.mousiki.shared.db.Historic_tracks
import com.mousiki.shared.domain.models.YtbTrack

typealias HistoricTrackEntity = Historic_tracks

fun Historic_tracks.toMusicTrack() = YtbTrack(
    youtubeId = youtube_id,
    title = title,
    duration = duration,
    artistName = title.split("-")[0]
)