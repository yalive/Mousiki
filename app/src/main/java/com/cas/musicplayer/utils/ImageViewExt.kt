package com.cas.musicplayer.utils

import android.widget.ImageView
import com.squareup.picasso.Picasso

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