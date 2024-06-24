package com.mousiki.shared.data.db

import com.mousiki.shared.data.models.UdioSong
import com.mousiki.shared.db.Custom_playlist_track
import com.mousiki.shared.domain.models.AiTrack
import com.mousiki.shared.domain.models.LocalSong
import com.mousiki.shared.domain.models.Song
import com.mousiki.shared.domain.models.Track
import com.mousiki.shared.domain.models.YtbTrack

typealias CustomPlaylistTrackEntity = Custom_playlist_track

fun Custom_playlist_track.toTrack(): Track {
    if (type == Track.TYPE_LOCAL_AUDIO) {
        return LocalSong(
            song = Song.emptySong.copy(
                id = track_id.toLongOrZero(),
                title = title,
                duration = duration.toLong(),
                artistName = artist_name,
                artistId = artist_id.toLongOrZero()
            )
        )
    } else if (type == Track.TYPE_AI_AUDIO) {
        return AiTrack(
            udioSong = UdioSong(
                id = track_id,
                title = title,
                userId = artist_id,
                artist = artist_name,
                songPath = stream_url,
                imageUrl = image_url,
                duration = duration.toDouble(),
                created_at = "",
                generation_id = "",
                published_at = "",
                video_path = "",
                finished = false,
                liked = false,
                disliked = false,
                publishable = false,
                tags = emptyList(),
                plays = -1,
                likes = -1,
                lyrics = "",
                prompt = ""

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