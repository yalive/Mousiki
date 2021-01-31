package com.mousiki.shared.data.datasource

import com.cas.musicplayer.MousikiDb
import com.mousiki.shared.data.db.toArtist
import com.mousiki.shared.data.models.Artist
import com.mousiki.shared.db.Artists
import com.mousiki.shared.db.ArtistsQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-24.
 ***************************************
 */
class ArtistsLocalDataSource(
    private val db: MousikiDb
) {
    private val artistQuery: ArtistsQueries = db.artistsQueries

    suspend fun getArtists(ids: List<String>): List<Artist> = withContext(Dispatchers.Default) {
        val artists = artistQuery.getByChannelIds(ids).executeAsList()
        return@withContext when (artists.size) {
            ids.size -> artists.map { it.toArtist() }
            else -> emptyList()
        }
    }

    suspend fun saveArtists(artists: List<Artist>) = withContext(Dispatchers.Default) {
        val artistsEntity = artists.map {
            Artists(
                id = 0,
                name = it.name,
                countryCode = "", // To be reviewed
                channel_id = it.channelId,
                urlImage = it.urlImage

            )
        }
        db.transaction {
            artistsEntity.forEach { artist ->
                artistQuery.insert(artist)
            }
        }
    }
}