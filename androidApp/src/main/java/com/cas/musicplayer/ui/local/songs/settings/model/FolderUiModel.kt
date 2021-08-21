package com.cas.musicplayer.ui.local.songs.settings.model

import com.cas.musicplayer.ui.local.folders.Folder
import com.mousiki.shared.domain.models.DisplayableItem

data class FolderUiModel(
    val folder: Folder,
    val subtitle: String,
    val hidden: Boolean,
    val listener: (Folder) -> Unit
) : DisplayableItem
