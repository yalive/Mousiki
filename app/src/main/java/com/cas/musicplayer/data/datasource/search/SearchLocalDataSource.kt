package com.cas.musicplayer.data.datasource.search

import com.cas.musicplayer.data.local.database.dao.SearchChannelDao
import com.cas.musicplayer.data.local.database.dao.SearchPlaylistsDao
import com.cas.musicplayer.data.local.database.dao.SearchSongDao
import com.cas.musicplayer.data.local.models.*
import com.mousiki.shared.domain.models.Channel
import com.mousiki.shared.domain.models.MusicTrack
import com.mousiki.shared.domain.models.Playlist
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-09.
 ***************************************
 */
class SearchLocalDataSource @Inject constructor(
    private val searchSongDao: SearchSongDao,
    private val searchPlaylistsDao: SearchPlaylistsDao,
    private val searchChannelDao: SearchChannelDao
) {

    suspend fun getSearchSongsResultForQuery(query: String): List<MusicTrack> {
        return searchSongDao.getResultForQuery(query).map {
            it.toMusicTrack()
        }
    }

    suspend fun saveSongs(query: String, songs: List<MusicTrack>) {
        val searchSongEntities = songs.map {
            SearchSongEntity(
                youtubeId = it.youtubeId,
                duration = it.duration,
                title = it.title,
                query = query
            )
        }
        searchSongDao.insert(searchSongEntities)
    }


    suspend fun getSearchPlaylistsResultForQuery(query: String): List<Playlist> {
        return searchPlaylistsDao.getResultForQuery(query).map {
            it.toPlaylist()
        }
    }

    suspend fun savePlaylists(query: String, songs: List<Playlist>) {
        val searchPlaylistEntities = songs.map {
            SearchPlaylistEntity(
                playlistId = it.id,
                itemCount = it.itemCount,
                urlImage = it.urlImage,
                title = it.title,
                query = query
            )
        }
        searchPlaylistsDao.insert(searchPlaylistEntities)
    }

    suspend fun getSearchChannelsResultForQuery(query: String): List<Channel> {
        return searchChannelDao.getResultForQuery(query).map {
            it.toChannel()
        }
    }

    suspend fun saveChannels(query: String, songs: List<Channel>) {
        val searchChannelEntities = songs.map {
            SearchChannelEntity(
                channelId = it.id,
                urlImage = it.urlImage,
                name = it.title,
                query = query
            )
        }
        searchChannelDao.insert(searchChannelEntities)
    }
}