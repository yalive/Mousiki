package com.mousiki.shared.data.db

import com.mousiki.shared.db.Playlist_tracks
import com.mousiki.shared.domain.models.MusicTrack

fun Playlist_tracks.toMusicTrack() = MusicTrack(
    youtubeId = youtube_id,
    title = title,
    duration = duration
)