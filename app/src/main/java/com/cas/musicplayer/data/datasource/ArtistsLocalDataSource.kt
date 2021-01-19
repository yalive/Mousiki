package com.cas.musicplayer.data.datasource

import com.cas.musicplayer.data.local.database.dao.ArtistDao
import com.cas.musicplayer.data.local.models.ArtistEntity
import com.cas.musicplayer.data.local.models.toArtist
import com.mousiki.shared.data.models.Artist
import com.cas.musicplayer.utils.bgContext
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-24.
 ***************************************
 */
class ArtistsLocalDataSource @Inject constructor(
    private val artistsDao: ArtistDao
) {
    suspend fun getArtists(ids: List<String>): List<Artist> = withContext(bgContext) {
        val artists = artistsDao.getByChannelIds(ids)
        return@withContext when {
            artists.size == ids.size -> artists.map { it.toArtist() }
            else -> emptyList()
        }
    }

    suspend fun saveArtists(artists: List<Artist>) = withContext(bgContext) {
        val artistsEntity = artists.map {
            ArtistEntity(
                name = it.name,
                countryCode = "", // To be reviewed
                channelId = it.channelId,
                urlImage = it.urlImage

            )
        }
        artistsDao.insert(artistsEntity)
    }
}