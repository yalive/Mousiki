package com.cas.musicplayer.ui.common

import android.graphics.ColorFilter
import androidx.core.view.isVisible
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.SimpleColorFilter
import com.airbnb.lottie.model.KeyPath
import com.airbnb.lottie.value.LottieValueCallback
import com.cas.musicplayer.R
import com.cas.musicplayer.utils.color
import com.mousiki.shared.domain.models.DisplayedVideoItem

/**
 ***************************************
 * Created by Y.Abdelhadi on 5/7/20.
 ***************************************
 */


fun LottieAnimationView.setMusicPlayingState(item: DisplayedVideoItem) {
    isVisible = item.isCurrent
    if (item.isCurrent && item.isPlaying) {
        post { playAnimation() }
    } else if (item.isCurrent) {
        pauseAnimation()
    } else {
        cancelAnimation()
    }

    // Set playing color!!
    val colorAccent = context.color(R.color.colorAccent)
    val filter = SimpleColorFilter(colorAccent)
    val keyPath = KeyPath("**")
    val callback = LottieValueCallback<ColorFilter>(filter)
    addValueCallback(keyPath, LottieProperty.COLOR_FILTER, callback)
}

fun LottieAnimationView.setLocalMusicPlayingState(item: DisplayedVideoItem) {
    isVisible = item.isCurrent
    if (item.isCurrent && item.isPlaying) {
        post { playAnimation() }
    } else if (item.isCurrent) {
        pauseAnimation()
    } else {
        cancelAnimation()
    }

    // Set playing color!!
    val localSongsPrimaryColor = context.color(R.color.localSongsPrimaryColor)
    val filter = SimpleColorFilter(localSongsPrimaryColor)
    val keyPath = KeyPath("**")
    val callback = LottieValueCallback<ColorFilter>(filter)
    addValueCallback(keyPath, LottieProperty.COLOR_FILTER, callback)
}