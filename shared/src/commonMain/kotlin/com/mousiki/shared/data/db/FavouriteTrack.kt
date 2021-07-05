package com.mousiki.shared.data.db

import com.mousiki.shared.db.Favourite_tracks
import com.mousiki.shared.domain.models.LocalSong
import com.mousiki.shared.domain.models.Song
import com.mousiki.shared.domain.models.Track
import com.mousiki.shared.domain.models.YtbTrack

typealias FavouriteTrackEntity = Favourite_tracks

fun Favourite_tracks.toTrack(): Track {
    val localId = try {
        youtube_id.toLong()
    } catch (e: Exception) {
        null
    }

    if (localId != null) {
        return LocalSong(
            song = Song.emptySong.copy(
                id = localId,
                title = title,
                duration = duration.toLong()
            )
        )
    }
    return YtbTrack(
        youtubeId = youtube_id,
        title = title,
        duration = duration,
        artistName = title.split("-")[0]
    )
}
