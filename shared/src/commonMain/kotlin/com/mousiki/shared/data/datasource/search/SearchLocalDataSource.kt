package com.mousiki.shared.data.datasource.search

import com.cas.musicplayer.MousikiDb
import com.mousiki.shared.data.db.toChannel
import com.mousiki.shared.data.db.toMusicTrack
import com.mousiki.shared.data.db.toPlaylist
import com.mousiki.shared.db.Channel_search_result
import com.mousiki.shared.db.Playlists_search_result
import com.mousiki.shared.db.Songs_search_result
import com.mousiki.shared.domain.models.Channel
import com.mousiki.shared.domain.models.MusicTrack
import com.mousiki.shared.domain.models.Playlist

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-09.
 ***************************************
 */
class SearchLocalDataSource(
    private val db: MousikiDb
) {

    private val searchSongDao by lazy { db.songsSearchResultQueries }
    private val searchPlaylistsDao by lazy { db.playlistsSearchResultQueries }
    private val searchChannelDao by lazy { db.channelSearchResultQueries }

    suspend fun getSearchSongsResultForQuery(query: String): List<MusicTrack> {
        return searchSongDao.getResultForQuery(query).executeAsList().map {
            it.toMusicTrack()
        }
    }

    suspend fun saveSongs(query: String, songs: List<MusicTrack>) {
        val searchSongEntities = songs.map {
            Songs_search_result(
                id = 0,
                youtube_id = it.youtubeId,
                duration = it.duration,
                title = it.title,
                query = query
            )
        }
        db.transaction {
            searchSongEntities.forEach { song ->
                searchSongDao.insert(song)
            }
        }
    }


    suspend fun getSearchPlaylistsResultForQuery(query: String): List<Playlist> {
        return searchPlaylistsDao.getResultForQuery(query).executeAsList().map {
            it.toPlaylist()
        }
    }

    suspend fun savePlaylists(query: String, songs: List<Playlist>) {
        val searchPlaylistEntities = songs.map {
            Playlists_search_result(
                id = 0,
                playlist_id = it.id,
                itemCount = it.itemCount.toLong(),
                urlImage = it.urlImage,
                title = it.title,
                query = query
            )
        }

        db.transaction {
            searchPlaylistEntities.forEach { playlist ->
                searchPlaylistsDao.insert(playlist)
            }
        }
    }

    suspend fun getSearchChannelsResultForQuery(query: String): List<Channel> {
        return searchChannelDao.getResultForQuery(query).executeAsList().map {
            it.toChannel()
        }
    }

    suspend fun saveChannels(query: String, songs: List<Channel>) {
        val searchChannelEntities = songs.map {
            Channel_search_result(
                id = 0,
                channel_id = it.id,
                urlImage = it.urlImage,
                name = it.title,
                query = query
            )
        }

        db.transaction {
            searchChannelEntities.forEach { channel ->
                searchChannelDao.insert(channel)
            }
        }
    }
}