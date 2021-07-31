/*
 * Copyright (c) 2019 Hemanth Savarala.
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by
 *  the Free Software Foundation either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package com.cas.musicplayer.ui.local.artists.model

import com.cas.musicplayer.utils.PreferenceUtil
import com.cas.musicplayer.utils.Utils
import com.mousiki.shared.domain.models.Album
import com.mousiki.shared.domain.models.Song


data class LocalArtist(
    val id: Long,
    val albums: List<Album>
) {

    val name: String
        get() {
            val artistName = safeGetFirstAlbum().safeGetFirstSong().artistName
            if (PreferenceUtil.albumArtistsOnly && Utils.isVariousArtists(artistName)) {
                return VARIOUS_ARTISTS_DISPLAY_NAME
            }
            return if (Utils.isArtistNameUnknown(artistName)) {
                UNKNOWN_ARTIST_DISPLAY_NAME
            } else artistName
        }

    val songCount: Int
        get() {
            var songCount = 0
            for (album in albums) {
                songCount += album.songCount
            }
            return songCount
        }

    val albumCount: Int
        get() = albums.size

    val songs: List<Song>
        get() = albums.flatMap { it.songs }

    fun safeGetFirstAlbum(): Album {
        return albums.firstOrNull() ?: Album.empty
    }

    companion object {
        const val UNKNOWN_ARTIST_DISPLAY_NAME = "Unknown Artist"
        const val VARIOUS_ARTISTS_DISPLAY_NAME = "Various Artists"
        const val VARIOUS_ARTISTS_ID: Long = -2
        val empty = LocalArtist(-1, emptyList())

    }
}
