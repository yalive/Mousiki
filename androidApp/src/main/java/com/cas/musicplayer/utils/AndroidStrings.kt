package com.cas.musicplayer.utils

import com.cas.musicplayer.MusicApp
import com.cas.musicplayer.R
import com.mousiki.shared.utils.Strings

object AndroidStrings : Strings {

    // Home Header
    override val titleNewRelease: String
        get() = MusicApp.get().getString(R.string.title_new_release)
    override val titleTopCharts: String
        get() = MusicApp.get().getString(R.string.title_top_charts)
    override val artists: String
        get() = MusicApp.get().getString(R.string.artists)
    override val genres: String
        get() = MusicApp.get().getString(R.string.genres)

    // Library
    override val emptyFavouriteList: String
        get() = MusicApp.get().getString(R.string.empty_favourite_list)
    override val libraryRecent: String
        get() = MusicApp.get().getString(R.string.library_recent)
    override val libraryFavourites: String
        get() = MusicApp.get().getString(R.string.library_favourites)
    override val libraryHeavySongs: String
        get() = MusicApp.get().getString(R.string.library_heavy_songs)
    override val libraryTitlePlaylist: String
        get() = MusicApp.get().getString(R.string.title_playlist)

    // Create playlist
    override fun playlistExist(playlistName: String): String {
        return MusicApp.get().getString(R.string.playlist_exist_message, playlistName)
    }

    override fun playlistCreated(playlistName: String): String {
        return MusicApp.get().getString(R.string.playlist_created, playlistName)
    }

    override fun trackAddedToPlaylist(playlistName: String): String {
        return MusicApp.get().getString(R.string.track_added_to_playlist, playlistName)
    }
}