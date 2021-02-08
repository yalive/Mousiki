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
}