package com.cas.musicplayer.utils

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.RemoteViews
import androidx.annotation.DrawableRes
import com.cas.musicplayer.R
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target


/**
 **********************************
 * Created by Abdelhadi on 2019-05-12.
 **********************************
 */

fun ImageView.loadBitmap(
    @DrawableRes resId: Int,
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
                bitmap?.let(onGetBitmap)
            } catch (e: OutOfMemoryError) {
            }
        }
    }
    this.tag = target
    Picasso.get().load(resId).into(target)
}

fun ImageView.loadBitmap(
    url: String,
    onGetBitmap: ((Bitmap) -> Unit) = {}
) {
    if (url.isEmpty()) return
    val target = object : Target {
        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
            // Nothing
        }

        override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
            // Nothing
        }

        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
            try {
                bitmap?.let(onGetBitmap)
            } catch (e: OutOfMemoryError) {
            }
        }
    }
    this.tag = target
    Picasso.get().load(url).into(target)
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

fun RemoteViews.loadAndBlurImage(
    idImageView: Int,
    urlImage: String,
    scale: Float = 0.3f,
    radius: Int = 45
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
                setImageViewBitmap(idImageView, BlurImage.fastblur(bitmap, scale, radius))
            } catch (e: OutOfMemoryError) {
            }
        }
    }
    //this.tag = target
    Picasso.get().load(urlImage).into(target)
}