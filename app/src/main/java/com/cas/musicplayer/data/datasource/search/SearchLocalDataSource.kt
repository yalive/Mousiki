package com.cas.musicplayer.data.datasource.search

import com.cas.musicplayer.data.local.database.dao.SearchSongDao
import com.cas.musicplayer.data.local.models.SearchSongEntity
import com.cas.musicplayer.data.local.models.toMusicTrack
import com.cas.musicplayer.domain.model.MusicTrack
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-09.
 ***************************************
 */
class SearchLocalDataSource @Inject constructor(
    private val searchSongDao: SearchSongDao
) {

    suspend fun getSearchResultForQuery(query: String): List<MusicTrack> {
        return searchSongDao.getResultForQuery(query).map {
            it.toMusicTrack()
        }
    }

    suspend fun saveRemoteSearchSongs(query: String, songs: List<MusicTrack>) {
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
}