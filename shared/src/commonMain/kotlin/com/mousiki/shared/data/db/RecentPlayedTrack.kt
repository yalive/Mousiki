package com.mousiki.shared.data.db

import com.mousiki.shared.db.Db_recentTrack
import com.mousiki.shared.domain.models.LocalSong
import com.mousiki.shared.domain.models.Song
import com.mousiki.shared.domain.models.Track
import com.mousiki.shared.domain.models.YtbTrack

typealias RecentPlayedTrack = Db_recentTrack

fun Db_recentTrack.toTrack(): Track {
    if (type == Track.TYPE_LOCAL_AUDIO) {
        return LocalSong(
            song = Song.emptySong.copy(
                id = track_id.toLongOrZero(),
                title = title,
                duration = duration.toLong()
            )
        )
    }

    return YtbTrack(
        youtubeId = track_id,
        title = title,
        duration = duration,
        artistName = title.split("-")[0]
    )
}