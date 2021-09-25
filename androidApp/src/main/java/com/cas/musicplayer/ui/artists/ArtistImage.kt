package com.cas.musicplayer.ui.artists

import android.util.DisplayMetrics
import com.cas.musicplayer.MusicApp
import com.mousiki.shared.data.models.Artist

val Artist.homeImagePath: String
    get() = imageFullPath.replace("s800", "s${getHomeArtistImageSize()}")

val Artist.thumbnail: String
    get() = imageFullPath.replace("s800", "s${getArtistThumbnailSize()}")

fun getHomeArtistImageSize(): Int {
    val context = MusicApp.get()
    val density = context.resources.displayMetrics.densityDpi
    return when {
        density <= DisplayMetrics.DENSITY_LOW -> 80
        density > DisplayMetrics.DENSITY_LOW && density <= DisplayMetrics.DENSITY_MEDIUM -> 100
        density > DisplayMetrics.DENSITY_MEDIUM && density <= DisplayMetrics.DENSITY_HIGH -> 120
        density > DisplayMetrics.DENSITY_HIGH && density <= DisplayMetrics.DENSITY_XHIGH -> 160
        density > DisplayMetrics.DENSITY_XHIGH && density <= DisplayMetrics.DENSITY_XXHIGH -> 200
        density > DisplayMetrics.DENSITY_XXHIGH && density <= DisplayMetrics.DENSITY_XXXHIGH -> 260
        else -> 300
    }
}

fun getArtistThumbnailSize(): Int {
    val context = MusicApp.get()
    val density = context.resources.displayMetrics.densityDpi
    return when {
        density <= DisplayMetrics.DENSITY_LOW -> 60
        density > DisplayMetrics.DENSITY_LOW && density <= DisplayMetrics.DENSITY_MEDIUM -> 60
        density > DisplayMetrics.DENSITY_MEDIUM && density <= DisplayMetrics.DENSITY_HIGH -> 70
        density > DisplayMetrics.DENSITY_HIGH && density <= DisplayMetrics.DENSITY_XHIGH -> 80
        density > DisplayMetrics.DENSITY_XHIGH && density <= DisplayMetrics.DENSITY_XXHIGH -> 90
        density > DisplayMetrics.DENSITY_XXHIGH && density <= DisplayMetrics.DENSITY_XXXHIGH -> 100
        else -> 120
    }
}