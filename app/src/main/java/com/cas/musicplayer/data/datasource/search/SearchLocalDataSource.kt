package com.cas.musicplayer.data.datasource.search

import com.cas.musicplayer.data.local.database.dao.SearchPlaylistsDao
import com.cas.musicplayer.data.local.database.dao.SearchSongDao
import com.cas.musicplayer.data.local.models.SearchPlaylistEntity
import com.cas.musicplayer.data.local.models.SearchSongEntity
import com.cas.musicplayer.data.local.models.toMusicTrack
import com.cas.musicplayer.data.local.models.toPlaylist
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.domain.model.Playlist
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-09.
 ***************************************
 */
class SearchLocalDataSource @Inject constructor(
    private val searchSongDao: SearchSongDao,
    private val searchPlaylistsDao: SearchPlaylistsDao
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
        val searchSongEntities = songs.map {
            SearchPlaylistEntity(
                playlistId = it.id,
                itemCount = it.itemCount,
                urlImage = it.urlImage,
                title = it.title,
                query = query
            )
        }
        searchPlaylistsDao.insert(searchSongEntities)
    }
}