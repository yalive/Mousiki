package com.cas.common.extensions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.transition.Fade
import androidx.transition.TransitionManager

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-12.
 ***************************************
 */

fun ViewGroup.inflate(@LayoutRes layoutId: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)
}

fun ViewGroup.hideSubViews(fade: Boolean = true) {
    if (fade) {
        TransitionManager.beginDelayedTransition(this, Fade(Fade.OUT))
    }
    forEach { childView ->
        childView.isVisible = false
    }
}

fun ViewGroup.showSubViews(fade: Boolean = true) {
    if (fade) {
        TransitionManager.beginDelayedTransition(this, Fade(Fade.IN))
    }
    forEach { childView ->
        childView.isVisible = true
    }
}