package com.cas.musicplayer.utils

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.RemoteViews
import androidx.annotation.DrawableRes
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target


/**
 **********************************
 * Created by Abdelhadi on 2019-05-12.
 **********************************
 */

fun ImageView.loadImage(urlImage: String) {
    if (urlImage.isNotEmpty()) {
        Picasso.get().load(urlImage)
            .fit()
            .into(this)
    }
}

fun ImageView.loadImage(
    urlImage: String, @DrawableRes placeHolder: Int? = null
) {

    if (urlImage.isNotEmpty()) {
        Picasso.get().load(urlImage)
            .apply {
                if (placeHolder != null) {
                    placeholder(placeHolder)
                }
            }
            .fit()
            .into(this)
    }
}

fun ImageView.loadAndBlurImage(urlImage: String, scale: Float = 0.3f, radius: Int = 45) {
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