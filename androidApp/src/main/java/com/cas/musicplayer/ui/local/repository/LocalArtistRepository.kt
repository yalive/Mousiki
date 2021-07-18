package com.cas.musicplayer.ui.local.repository

import android.provider.MediaStore
import com.cas.musicplayer.ui.local.artists.model.LocalArtist
import com.cas.musicplayer.utils.PreferenceUtil
import com.mousiki.shared.domain.models.Album
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by Fayssel Yabahddou on 6/25/21.
 */
class LocalArtistRepository(
    private val songRepository: LocalSongsRepository,
    private val albumRepository: AlbumRepository
) {

    private fun getSongLoaderSortOrder(): String {
        return PreferenceUtil.artistSortOrder + ", " +
                PreferenceUtil.artistAlbumSortOrder + ", " +
                PreferenceUtil.artistSongSortOrder
    }

    fun artist(artistId: Long): LocalArtist {
        if (artistId == LocalArtist.VARIOUS_ARTISTS_ID) {
            // Get Various Artists
            val songs = songRepository.songs(
                songRepository.makeSongCursor(
                    null,
                    null,
                    getSongLoaderSortOrder()
                )
            )
            val albums = albumRepository.splitIntoAlbums(songs)
                .filter { it.albumArtist == LocalArtist.VARIOUS_ARTISTS_DISPLAY_NAME }
            return LocalArtist(LocalArtist.VARIOUS_ARTISTS_ID, albums)
        }

        val songs = songRepository.songs(
            songRepository.makeSongCursor(
                MediaStore.Audio.AudioColumns.ARTIST_ID + "=?",
                arrayOf(artistId.toString()),
                getSongLoaderSortOrder()
            )
        )
        return LocalArtist(artistId, albumRepository.splitIntoAlbums(songs))
    }

    suspend fun artists(): List<LocalArtist> = withContext(Dispatchers.IO) {
        val songs = songRepository.songs(
            songRepository.makeSongCursor(
                null, null,
                getSongLoaderSortOrder()
            )
        )
        return@withContext splitIntoArtists(albumRepository.splitIntoAlbums(songs))
    }

    fun albumArtists(): List<LocalArtist> {
        val songs = songRepository.songs(
            songRepository.makeSongCursor(
                null,
                null,
                getSongLoaderSortOrder()
            )
        )

        return splitIntoAlbumArtists(albumRepository.splitIntoAlbums(songs))
    }

    fun artists(query: String): List<LocalArtist> {
        val songs = songRepository.songs(
            songRepository.makeSongCursor(
                MediaStore.Audio.AudioColumns.ARTIST + " LIKE ?",
                arrayOf("%$query%"),
                getSongLoaderSortOrder()
            )
        )
        return splitIntoArtists(albumRepository.splitIntoAlbums(songs))
    }


    private fun splitIntoAlbumArtists(albums: List<Album>): List<LocalArtist> {
        return albums.groupBy { it.albumArtist }
            .map {
                val currentAlbums = it.value
                if (currentAlbums.isNotEmpty()) {
                    if (currentAlbums[0].albumArtist == LocalArtist.VARIOUS_ARTISTS_DISPLAY_NAME) {
                        LocalArtist(LocalArtist.VARIOUS_ARTISTS_ID, currentAlbums)
                    } else {
                        LocalArtist(currentAlbums[0].artistId, currentAlbums)
                    }
                } else {
                    LocalArtist.empty
                }
            }
    }


    fun splitIntoArtists(albums: List<Album>): List<LocalArtist> {
        return albums.groupBy { it.artistId }
            .map { LocalArtist(it.key, it.value) }
    }
}