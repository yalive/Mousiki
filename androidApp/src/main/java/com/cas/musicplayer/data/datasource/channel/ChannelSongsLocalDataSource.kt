package com.cas.musicplayer.data.datasource.channel

import com.cas.musicplayer.data.local.database.dao.ChannelSongsDao
import com.cas.musicplayer.data.local.models.ChannelSongEntity
import com.cas.musicplayer.data.local.models.toMusicTrack
import com.mousiki.shared.domain.models.MusicTrack
import com.cas.musicplayer.utils.bgContext
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-24.
 ***************************************
 */

class ChannelSongsLocalDataSource @Inject constructor(
    private val channelSongsDao: ChannelSongsDao
) {

    suspend fun getChannelSongs(channelId: String): List<MusicTrack> = withContext(bgContext) {
        return@withContext channelSongsDao.getChannelTracks(channelId).map {
            it.toMusicTrack()
        }
    }

    suspend fun saveChannelSongs(channelId: String, tracks: List<MusicTrack>) =
        withContext(bgContext) {
            val channelSongs = tracks.map {
                ChannelSongEntity(
                    youtubeId = it.youtubeId,
                    channelId = channelId,
                    title = it.title,
                    duration = it.duration
                )
            }
            channelSongsDao.insert(channelSongs)
        }
}