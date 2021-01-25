package com.cas.musicplayer.utils

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.widget.ImageViewCompat
import com.cas.musicplayer.R
import com.cas.musicplayer.ui.common.songs.AppImage
import com.mousiki.shared.domain.models.MusicTrack
import com.mousiki.shared.domain.models.imgUrlDef0
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume


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

fun ImageView.loadTrackImage(
    track: MusicTrack
) {
    val url = UserPrefs.getTrackImageUrl(track)
    if (url.isNotEmpty()) {
        Picasso.get().load(url)
            .placeholder(R.drawable.app_icon_placeholder)
            .fit()
            .into(this, object : Callback {
                override fun onSuccess() {
                    UserPrefs.setTrackImageUrl(track, url)
                }

                override fun onError(e: java.lang.Exception?) {
                    Picasso.get().load(track.imgUrlDef0)
                        .error(R.drawable.app_icon_placeholder)
                        .fit()
                        .into(this@loadTrackImage)
                    UserPrefs.setTrackImageUrl(track, track.imgUrlDef0)
                }
            })
    } else {
        setImageResource(R.drawable.app_icon_placeholder)
    }
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

suspend fun Picasso.getBitmap(url: String, maxHeight: Int): Bitmap? =
    suspendCancellableCoroutine { continuation ->
        if (url.isEmpty()) {
            continuation.resume(null)
            return@suspendCancellableCoroutine
        }
        val target = createTargetWith(continuation)
        load(url).apply {
            resize(0, maxHeight)
        }.into(target)
    }

suspend fun ImageView.getBitmap(url: String, maxHeight: Int): Bitmap? =
    suspendCancellableCoroutine { continuation ->
        if (url.isEmpty()) {
            continuation.resume(null)
            return@suspendCancellableCoroutine
        }
        val target = createTargetWith(continuation)
        this.tag = target
        Picasso.get().load(url).resize(0, maxHeight).into(target)
    }

suspend fun ImageView.getBitmap(appImage: AppImage, maxHeight: Int): Bitmap? =
    suspendCancellableCoroutine { continuation ->
        if (appImage is AppImage.AppImageUrl && appImage.url.isEmpty()) {
            continuation.resume(null)
            return@suspendCancellableCoroutine
        }
        val target = createTargetWith(continuation)
        this.tag = target
        val picasso = Picasso.get()
        when (appImage) {
            is AppImage.AppImageRes -> {
                picasso.load(appImage.resId).resize(0, maxHeight).into(target)
            }
            is AppImage.AppImageUrl -> {
                picasso.load(appImage.url).resize(0, maxHeight).into(target)
            }
            is AppImage.AppImageName -> {
                val resourceId: Int = resources.getIdentifier(
                    appImage.name, "drawable", context.packageName
                )
                picasso.load(resourceId).resize(0, maxHeight).into(target)
            }
        }
    }

private fun createTargetWith(continuation: CancellableContinuation<Bitmap?>): Target {
    return object : Target {
        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
            // Nop
        }

        override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {
            if (continuation.isActive) {
                continuation.resume(null)
            }
        }

        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
            if (continuation.isActive) {
                continuation.resume(bitmap)
            }
        }
    }
}

fun ImageView.updateBitmap(
    new_image: Bitmap?
) {
    val animOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out)
    val animIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in)
    animIn.duration = 1000
    animOut.duration = 500
    animOut.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation?) {}
        override fun onAnimationRepeat(animation: Animation?) {}
        override fun onAnimationEnd(animation: Animation?) {
            setImageBitmap(new_image)
            animIn.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationRepeat(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {}
            })
            startAnimation(animIn)
        }
    })
    startAnimation(animOut)
}