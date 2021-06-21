package com.mousiki.shared.domain.models

import android.content.ContentUris
import android.net.Uri

actual val LocalSong.albumImage: String
    get() {
        val withAppendedId = ContentUris.withAppendedId(
            Uri.parse("content://media/external/audio/albumart"),
            albumId
        )
        return withAppendedId.toString()
    }