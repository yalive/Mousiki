package com.mousiki.shared.data.repository

import com.cas.musicplayer.MousikiDb
import com.mousiki.shared.data.db.RecentPlayedTrack
import com.mousiki.shared.data.db.toLongOrZero
import com.mousiki.shared.data.db.toTrack
import com.mousiki.shared.db.DbRecentPlayedVideo
import com.mousiki.shared.db.Db_recentTrack
import com.mousiki.shared.domain.models.AiTrack
import com.mousiki.shared.domain.models.LocalSong
import com.mousiki.shared.domain.models.Song
import com.mousiki.shared.domain.models.Track
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-26.
 ***************************************
 */
class StatisticsRepository(
    private val db: MousikiDb
) {
    private val recentTracksDao by lazy { db.recentPlayedTracksQueries }
    private val recentVideosDao by lazy { db.recentPlayedVideoQueries }


    suspend fun addTrackToRecent(track: Track) {
        val recentTrack = recentTracksDao.getByTrackId(track.id).executeAsOneOrNull()
        val playCount = if (recentTrack != null) recentTrack.play_count + 1 else 1
        val streamUrl = if (track is AiTrack) track.streamUrl else ""
        val imageUrl = if (track is AiTrack) track.image else ""
        recentTracksDao.insert(
            RecentPlayedTrack(
                id = 0,
                track_id = track.id,
                title = track.title,
                duration = track.duration,
                play_count = playCount,
                type = track.type,
                artist_name = track.artistName,
                artist_id = track.artistId,
                stream_url = streamUrl,
                image_url = imageUrl
            )
        )
    }

    suspend fun addVideoToRecent(track: Track) = withContext(Dispatchers.Default) {
        val recentVideo = recentVideosDao.getByVideoId(track.id).executeAsOneOrNull()
        val playCount = if (recentVideo != null) recentVideo.play_count + 1 else 1
        recentVideosDao.insert(
            DbRecentPlayedVideo(
                id = 0,
                video_id = track.id,
                duration = track.duration.toLong(),
                play_count = playCount,
                stop_at = 0
            )
        )
    }

    suspend fun removeTrackFromRecent(trackId: String) {
        recentTracksDao.deleteTrack(
            trackId
        )
    }

    suspend fun removeVideoFromRecent(videoId: String) {
        recentVideosDao.deleteVideo(
            videoId
        )
    }

    suspend fun getRecentlyPlayedTracks(max: Int = 10): List<Track> {
        return recentTracksDao.getTracks(max.toLong()).executeAsList().map {
            it.toTrack()
        }
    }

    fun getRecentlyPlayedTracksFlow(max: Int = 10): Flow<List<Track>> {
        return recentTracksDao.getTracks(max.toLong())
            .asFlow()
            .mapToList()
            .map { it.map(RecentPlayedTrack::toTrack) }
            .flowOn(Dispatchers.Default)
    }

    fun getRecentlyPlayedVideosFlow(max: Int = 10): Flow<List<Track>> {
        return recentVideosDao.getVideos(max.toLong())
            .asFlow()
            .mapToList()
            .map {
                it.map { dbVideo ->
                    LocalSong(
                        song = Song.emptySong.copy(
                            id = dbVideo.video_id.toLongOrZero(),
                            duration = dbVideo.duration,
                        )
                    )
                }
            }.flowOn(Dispatchers.Default)
    }

    suspend fun getRecentlyPlayedVideos(
        max: Int = 10
    ): List<Track> = withContext(Dispatchers.Default) {
        return@withContext recentVideosDao.getVideos(max.toLong())
            .executeAsList()
            .map { dbVideo ->
                LocalSong(
                    song = Song.emptySong.copy(
                        id = dbVideo.video_id.toLongOrZero(),
                        duration = dbVideo.duration,
                    )
                )
            }
    }

    suspend fun getHeavyList(max: Int = 10): List<Track> {
        return recentTracksDao.getHeavyList(max.toLong()).executeAsList().map {
            it.toTrack()
        }
    }

    fun getHeavyListFlow(max: Int = 10): Flow<List<Track>> {
        return recentTracksDao.getHeavyList(max.toLong())
            .asFlow()
            .mapToList()
            .map { it.map(Db_recentTrack::toTrack) }
            .flowOn(Dispatchers.Default)
    }

}