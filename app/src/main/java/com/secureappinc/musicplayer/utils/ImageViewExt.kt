package com.secureappinc.musicplayer.utils

import android.widget.ImageView
import com.secureappinc.musicplayer.data.models.SnippetVideo
import com.squareup.picasso.Picasso

/**
 **********************************
 * Created by Abdelhadi on 2019-05-12.
 **********************************
 */

fun ImageView.loadImage(snippetVideo: SnippetVideo?) {
    val urlImage = snippetVideo?.urlImageOrEmpty()
    if (urlImage != null && urlImage.isNotEmpty()) {
        Picasso.get().load(urlImage)
            .fit()
            .into(this)
    }
}