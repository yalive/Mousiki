package com.cas.musicplayer.player

import android.content.Context
import androidx.annotation.DrawableRes
import com.mousiki.shared.player.PlaySort

@DrawableRes
fun PlaySort.iconId(context: Context): Int {
    val resources = context.resources
    return resources.getIdentifier(
        iconName, "drawable", context.packageName
    )
}