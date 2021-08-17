package com.cas.musicplayer.utils

import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import com.mousiki.shared.domain.models.Song

fun Song.getFileUri(): Uri {
    return ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
}