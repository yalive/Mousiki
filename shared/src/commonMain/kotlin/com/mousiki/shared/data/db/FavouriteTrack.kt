package com.mousiki.shared.data.db

import com.mousiki.shared.db.Db_favouriteTrack
import com.mousiki.shared.domain.models.LocalSong
import com.mousiki.shared.domain.models.Song
import com.mousiki.shared.domain.models.Track
import com.mousiki.shared.domain.models.YtbTrack

typealias FavouriteTrackEntity = Db_favouriteTrack

fun Db_favouriteTrack.toTrack(): Track {
    if (type == Track.TYPE_LOCAL_AUDIO) {
        return LocalSong(
            song = Song.emptySong.copy(
                id = track_id.toLongOrZero(),
                title = title,
                duration = duration.toLong(),
                artistId = artist_id.toLongOrZero(),
                artistName = artist_name
            )
        )
    }
    return YtbTrack(
        youtubeId = track_id,
        title = title,
        duration = duration,
        artistName = artist_name,
        artistId = artist_id
    )
}

fun String.toLongOrZero(): Long {
    return try {
        toLong()
    } catch (e: Exception) {
        0
    }
}
