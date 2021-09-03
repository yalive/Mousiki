package com.cas.musicplayer.ui.local.folders

import android.content.Context
import com.cas.musicplayer.utils.fixedName
import com.cas.musicplayer.utils.fixedPath
import com.mousiki.shared.domain.models.Song
import java.io.File


enum class FolderType {
    SONG,
    VIDEO
}