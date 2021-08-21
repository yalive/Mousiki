package com.cas.musicplayer.utils

import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner

fun Fragment.onBackPressCallback(
    owner: LifecycleOwner = this,
    enabled: Boolean = true,
    onBackPressed: OnBackPressedCallback.() -> Unit
): OnBackPressedCallback? {
    return activity?.onBackPressedDispatcher?.addCallback(owner, enabled, onBackPressed)
}