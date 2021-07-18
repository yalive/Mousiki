package com.cas.musicplayer.ui.local.repository

import android.provider.MediaStore
import com.cas.musicplayer.utils.PreferenceUtil
import com.cas.musicplayer.utils.SortOrder
import com.mousiki.shared.domain.models.Album
import com.mousiki.shared.domain.models.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by Fayssel Yabahddou on 6/25/21.
 */
class AlbumRepository(private val songRepository: LocalSongsRepository) {

    suspend fun albums(): List<Album> = withContext(Dispatchers.IO) {
        val songs = songRepository.songs(
            songRepository.makeSongCursor(
                null,
                null,
                getSongLoaderSortOrder()
            )
        )
        return@withContext splitIntoAlbums(songs)
    }

    fun albums(query: String): List<Album> {
        val songs = songRepository.songs(
            songRepository.makeSongCursor(
                MediaStore.Audio.AudioColumns.ALBUM + " LIKE ?",
                arrayOf("%$query%"),
                getSongLoaderSortOrder()
            )
        )
        return splitIntoAlbums(songs)
    }

    fun album(albumId: Long): Album {
        val cursor = songRepository.makeSongCursor(
            MediaStore.Audio.AudioColumns.ALBUM_ID + "=?",
            arrayOf(albumId.toString()),
            getSongLoaderSortOrder()
        )
        val songs = songRepository.songs(cursor)
        val album = Album(albumId, songs)
        sortAlbumSongs(album)
        return album
    }

    fun splitIntoAlbums(
        songs: List<Song>
    ): List<Album> {
        return songs.groupBy { it.albumId }
            .map { sortAlbumSongs(Album(it.key, it.value)) }
    }

    private fun sortAlbumSongs(album: Album): Album {
        val songs = when (PreferenceUtil.albumDetailSongSortOrder) {
            SortOrder.AlbumSongSortOrder.SONG_TRACK_LIST -> album.songs.sortedWith { o1, o2 ->
                o1.trackNumber.compareTo(o2.trackNumber)
            }
            SortOrder.AlbumSongSortOrder.SONG_A_Z -> album.songs.sortedWith { o1, o2 ->
                o1.title.compareTo(o2.title)
            }
            SortOrder.AlbumSongSortOrder.SONG_Z_A -> album.songs.sortedWith { o1, o2 ->
                o2.title.compareTo(o1.title)
            }
            SortOrder.AlbumSongSortOrder.SONG_DURATION -> album.songs.sortedWith { o1, o2 ->
                o1.duration.compareTo(o2.duration)
            }
            else -> throw IllegalArgumentException("invalid ${PreferenceUtil.albumDetailSongSortOrder}")
        }
        return album.copy(songs = songs)
    }

    private fun getSongLoaderSortOrder(): String {
        return PreferenceUtil.albumSortOrder + ", " +
                PreferenceUtil.albumSongSortOrder
    }
}