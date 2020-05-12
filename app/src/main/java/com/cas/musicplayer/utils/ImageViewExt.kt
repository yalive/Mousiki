package com.cas.musicplayer.utils

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.widget.ImageViewCompat
import com.cas.musicplayer.R
import com.cas.musicplayer.ui.common.songs.AppImage
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


/**
 **********************************
 * Created by Abdelhadi on 2019-05-12.
 **********************************
 */


fun ImageView.tint(@ColorRes colorId: Int) {
    val color = context.color(colorId)
    tintColor(color)
}

fun ImageView.tintColor(color: Int) {
    ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(color))
}

fun ImageView.loadImage(
    urlImage: String,
    @DrawableRes placeHolder: Int? = R.drawable.ic_music_note,
    @DrawableRes errorImage: Int? = R.drawable.ic_music_note,
    onError: (() -> Unit) = {}
) {
    if (urlImage.isNotEmpty()) {
        Picasso.get().load(urlImage)
            .apply {
                placeHolder?.let {
                    placeholder(placeHolder)
                }
                errorImage?.let {
                    error(errorImage)
                }
            }
            .fit()
            .into(this, object : Callback {
                override fun onSuccess() {
                }

                override fun onError(e: java.lang.Exception?) {
                    onError()
                }
            })
    } else {
        placeHolder?.let {
            setImageResource(placeHolder)
        }
    }
}

suspend fun Picasso.getBitmap(url: String): Bitmap? = suspendCoroutine { continuation ->
    if (url.isEmpty()) {
        continuation.resume(null)
        return@suspendCoroutine
    }
    val target = createTargetWith(continuation)
    load(url).into(target)
}

suspend fun ImageView.getBitmap(url: String): Bitmap? =
    suspendCoroutine { continuation ->
        if (url.isEmpty()) {
            continuation.resume(null)
            return@suspendCoroutine
        }
        val target = createTargetWith(continuation)
        this.tag = target
        Picasso.get().load(url).into(target)
    }

suspend fun ImageView.getBitmap(appImage: AppImage): Bitmap? =
    suspendCoroutine { continuation ->
        if (appImage is AppImage.AppImageUrl && appImage.url.isEmpty()) {
            continuation.resume(null)
            return@suspendCoroutine
        }
        val target = createTargetWith(continuation)
        this.tag = target
        val picasso = Picasso.get()
        when (appImage) {
            is AppImage.AppImageRes -> picasso.load(appImage.resId).into(target)
            is AppImage.AppImageUrl -> picasso.load(appImage.url).into(target)
        }
    }

private fun createTargetWith(continuation: Continuation<Bitmap?>): Target {
    return object : Target {
        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
            // Nop
        }

        override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {
            continuation.resume(null)
        }

        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
            continuation.resume(bitmap)
        }
    }
}