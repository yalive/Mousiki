package com.cas.musicplayer.ui.local.videos.settings

import androidx.core.os.bundleOf
import androidx.recyclerview.widget.DiffUtil
import com.cas.musicplayer.delegateadapter.MousikiAdapter
import com.cas.musicplayer.ui.local.folders.settings.FilterFolderAdapterAdapterDelegate
import com.cas.musicplayer.ui.local.folders.settings.FolderUiModel
import com.cas.musicplayer.ui.local.songs.settings.delegate.FilterAudioSettingsItem
import com.cas.musicplayer.ui.local.songs.settings.delegate.FilterVideoSettingsAdapterDelegate
import com.mousiki.shared.domain.models.DisplayableItem

class LocalVideosSettingsAdapter : MousikiAdapter(
    listOf(
        FilterVideoSettingsAdapterDelegate(),
        FilterFolderAdapterAdapterDelegate()
    ),
    VideosSettingsDiffCallback()
)

class VideosSettingsDiffCallback : DiffUtil.ItemCallback<DisplayableItem>() {
    override fun areItemsTheSame(oldItem: DisplayableItem, newItem: DisplayableItem): Boolean {
        if (oldItem is FilterAudioSettingsItem && newItem is FilterAudioSettingsItem) {
            return true
        }
        if (oldItem is FolderUiModel && newItem is FolderUiModel) {
            return oldItem.folder == newItem.folder
        }
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: DisplayableItem, newItem: DisplayableItem): Boolean {
        if (oldItem is FilterAudioSettingsItem && newItem is FilterAudioSettingsItem) {
            return oldItem.filterLessDuration == newItem.filterLessDuration && oldItem.filterLessThanSize == newItem.filterLessThanSize
        }
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: DisplayableItem, newItem: DisplayableItem): Any? {
        if (oldItem is FilterAudioSettingsItem && newItem is FilterAudioSettingsItem) {
            return bundleOf()
        }

        if (oldItem is FolderUiModel && newItem is FolderUiModel && oldItem.hidden != newItem.hidden) {
            return bundleOf()
        }
        return super.getChangePayload(oldItem, newItem)
    }
}