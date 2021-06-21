package com.mousiki.shared.data.db

import com.mousiki.shared.db.Historic_tracks
import com.mousiki.shared.domain.models.MusicTrack

typealias HistoricTrackEntity = Historic_tracks

fun Historic_tracks.toMusicTrack() = MusicTrack(
    youtubeId = youtube_id,
    title = title,
    duration = duration
)