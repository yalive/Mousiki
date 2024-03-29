package com.mousiki.shared.utils

interface Strings {

    // Home Header
    val titleNewRelease: String
    val titleTopCharts: String
    val artists: String
    val genres: String

    // Library
    val emptyFavouriteList: String
    val libraryRecent: String
    val libraryFavourites: String
    val libraryHeavySongs: String
    val libraryTitlePlaylist: String

    // Create playlist
    fun playlistExist(playlistName: String): String
    fun playlistCreated(playlistName: String): String
    fun trackAddedToPlaylist(playlistName: String): String
}