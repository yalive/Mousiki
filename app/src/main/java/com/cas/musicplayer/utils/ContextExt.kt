package com.cas.musicplayer.utils

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import com.crashlytics.android.Crashlytics

/**
 **********************************
 * Created by Abdelhadi on 4/6/19.
 **********************************
 */

data class ScreenSize(val widthPx: Int, val heightPx: Int)

fun Context.dpToPixel(dp: Float): Int {
    return com.cas.common.dpToPixel(dp, this).toInt()
}

fun Context.pixelsToDp(px: Float): Float {
    return com.cas.common.pixelsToDp(px, this)
}

@ColorInt
fun Context.color(@ColorRes id: Int): Int {
    return ContextCompat.getColor(this, id)
}

fun Context.drawable(@DrawableRes id: Int): Drawable? {
    return try {
        ContextCompat.getDrawable(this, id)
    } catch (e: OutOfMemoryError) {
        Crashlytics.logException(e)
        null
    }
}

fun Context.themeColor(@AttrRes attrRes: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attrRes, typedValue, true)
    return typedValue.data
}

fun Context.canDrawOverApps() =
    (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(
        this
    ))

val windowOverlayTypeOrPhone: Int
    get() = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else -> WindowManager.LayoutParams.TYPE_PHONE
    }

val windowOverlayTypeOrSysAlert: Int
    get() = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else -> WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
    }

fun Context.screenSize(): ScreenSize {

    //Add the view to the window
    val windowManager = getSystemService(LifecycleService.WINDOW_SERVICE) as WindowManager?

    val displayMetrics = DisplayMetrics()

    windowManager?.defaultDisplay?.getMetrics(displayMetrics)
    val h = displayMetrics.heightPixels
    val w = displayMetrics.widthPixels

    return ScreenSize(w, h)
}

/* Toast */
fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.toast(@StringRes stringRes: Int) {
    Toast.makeText(this, stringRes, Toast.LENGTH_SHORT).show()
}

fun Context.longToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Context.toastCentred(message: String) {
    val toast = Toast.makeText(this, message, Toast.LENGTH_LONG)
    toast.setGravity(Gravity.CENTER, 0, 0)
    toast.show()
}

fun View?.visibleInScreen(): Boolean {
    if (this == null) {
        return false
    }
    if (!this.isShown) {
        return false
    }
    val actualPosition = Rect()
    this.getGlobalVisibleRect(actualPosition)
    val screen = Rect(
        0,
        0,
        this.context.screenSize().widthPx,
        this.context.screenSize().heightPx
    )
    return actualPosition.intersect(screen)
}
