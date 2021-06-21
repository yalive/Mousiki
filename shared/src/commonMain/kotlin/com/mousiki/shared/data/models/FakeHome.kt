package com.mousiki.shared.data.models

fun simplePlaylists(): List<SimplePlaylist> {
    return listOf(
        SimplePlaylist(
            title = "Top 100 Songs Global"
        ),
        SimplePlaylist(
            title = "Top 100 Songs US"
        ),
        SimplePlaylist(
            title = "Top 100 Songs Global"
        ),
        SimplePlaylist(
            title = "Top 100 Songs US"
        ),
        SimplePlaylist(
            title = "Top 100 Songs Global"
        ),
        SimplePlaylist(
            title = "Top 100 Songs US"
        )
    )
}

fun compactPlaylists(): List<CompactPlaylist> {
    return listOf(
        CompactPlaylist(
            title = "RELEASED",
            description = "",
            videoCount = "",
            playlistId = "",
            featuredImage = ""
        ),
        CompactPlaylist(
            title = "The Hit List",
            description = "",
            videoCount = "",
            playlistId = "",
            featuredImage = ""
        ),
        CompactPlaylist(
            title = "Al Millón",
            description = "",
            videoCount = "",
            playlistId = "",
            featuredImage = ""
        )
    )
}

fun fakeArtists(): List<Artist> {
    return listOf(
        Artist("", "", ""),
        Artist("", "", ""),
        Artist("", "", ""),
        Artist("", "", ""),
        Artist("", "", ""),
        Artist("", "", ""),
        Artist("", "", ""),
        Artist("", "", ""),
        Artist("", "", ""),
    )
}