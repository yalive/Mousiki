package com.mousiki.shared.data.db

import com.mousiki.shared.db.Playlist_tracks
import com.mousiki.shared.domain.models.YtbTrack

typealias PlaylistTrackEntity = Playlist_tracks

fun Playlist_tracks.toMusicTrack() = YtbTrack(
    youtubeId = youtube_id,
    title = title,
    duration = duration,
    artistName = title.split("-")[0]
)