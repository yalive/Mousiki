package com.mousiki.shared.data.db

import com.mousiki.shared.db.Favourite_tracks
import com.mousiki.shared.domain.models.MusicTrack

typealias FavouriteTrackEntity = Favourite_tracks

fun Favourite_tracks.toMusicTrack() = MusicTrack(
    youtubeId = youtube_id,
    title = title,
    duration = duration,
    artistName = title.split("-")[0]
)
