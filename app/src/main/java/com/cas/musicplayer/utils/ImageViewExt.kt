package com.cas.musicplayer.utils

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
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

fun ImageView.loadAndBlurImage(
    urlImage: String,
    scale: Float = 0.3f,
    radius: Int = 45,
    onGetBitmap: ((Bitmap) -> Unit) = {}
) {
    val target = object : Target {
        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
            // Nothing
        }

        override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
            // Nothing
        }

        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
            try {
                setImageBitmap(BlurImage.fastblur(bitmap, scale, radius))
                bitmap?.let(onGetBitmap)
            } catch (e: OutOfMemoryError) {
            }
        }
    }
    this.tag = target
    Picasso.get().load(urlImage).into(target)
}

suspend fun Picasso.getBitmap(url: String): Bitmap? = suspendCoroutine { continuation ->
    val target = createTargetWith(continuation)
    load(url).into(target)
}

suspend fun ImageView.getBitmap(url: String): Bitmap? =
    suspendCoroutine { continuation ->
        val target = createTargetWith(continuation)
        this.tag = target
        Picasso.get().load(url).into(target)
    }

suspend fun ImageView.getBitmap(appImage: AppImage): Bitmap? =
    suspendCoroutine { continuation ->
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