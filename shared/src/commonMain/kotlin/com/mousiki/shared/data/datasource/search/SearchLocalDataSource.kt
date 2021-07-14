package com.mousiki.shared.data.datasource.search

import com.cas.musicplayer.MousikiDb
import com.mousiki.shared.data.db.SongSearchEntity
import com.mousiki.shared.data.db.toTrack
import com.mousiki.shared.domain.models.YtbTrack

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-09.
 ***************************************
 */
class SearchLocalDataSource(
    private val db: MousikiDb
) {

    private val searchSongDao by lazy { db.songsSearchResultQueries }

    suspend fun getSearchSongsResultForQuery(query: String): List<YtbTrack> {
        return searchSongDao.getResultForQuery(query).executeAsList().map {
            it.toTrack()
        }
    }

    suspend fun saveSongs(query: String, songs: List<YtbTrack>) {
        val searchSongEntities = songs.map {
            SongSearchEntity(
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
}