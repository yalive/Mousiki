package com.cas.musicplayer.ui.local.videos.settings

import androidx.lifecycle.viewModelScope
import com.cas.musicplayer.ui.local.folders.Folder
import com.cas.musicplayer.ui.local.folders.FolderType
import com.cas.musicplayer.ui.local.folders.settings.FolderUiModel
import com.cas.musicplayer.ui.local.folders.shortPath
import com.cas.musicplayer.ui.local.repository.FoldersRepository
import com.cas.musicplayer.ui.local.songs.settings.delegate.FilterAudioSettingsItem
import com.cas.musicplayer.utils.PreferenceUtil
import com.mousiki.shared.domain.models.DisplayableItem
import com.mousiki.shared.ui.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LocalVideosSettingsViewModel(
    private val foldersRepository: FoldersRepository
) : BaseViewModel() {

    private val _settingItems = MutableStateFlow<List<DisplayableItem>?>(null)
    val settingItems: StateFlow<List<DisplayableItem>?> = _settingItems

    init {
        prepare()
    }

    private fun prepare() = viewModelScope.launch {
        val filterItem = FilterAudioSettingsItem(
            filterLessDuration = PreferenceUtil.filterVideoLessThanDuration,
            filterLessThanSize = PreferenceUtil.filterVideoLessThanSize,
            toggleMinDuration = ::onToggleLessThanDuration,
            toggleMinSize = ::onToggleLessThanLength
        )
        val items: List<DisplayableItem> = listOf(filterItem)
        _settingItems.value = items

        val folders = foldersRepository.getFolders(FolderType.VIDEO, true).map { folder ->
            val hidden = PreferenceUtil.isVideoFolderHidden(folder.path)
            FolderUiModel(folder, folder.shortPath, hidden, ::onClickFolder)
        }

        _settingItems.value = _settingItems.value.orEmpty().toMutableList()
            .apply { addAll(folders) }
    }

    private fun onClickFolder(folder: Folder) {
        PreferenceUtil.toggleVideosFolderVisibility(folder.path)
        _settingItems.value = _settingItems.value.orEmpty().map {
            when (it) {
                is FolderUiModel -> if (folder == it.folder) it.copy(hidden = !it.hidden) else it
                else -> it
            }
        }
    }

    private fun onToggleLessThanDuration() {
        PreferenceUtil.filterVideoLessThanDuration = !PreferenceUtil.filterVideoLessThanDuration
        _settingItems.value = _settingItems.value.orEmpty().map {
            when (it) {
                is FilterAudioSettingsItem -> it.copy(filterLessDuration = PreferenceUtil.filterVideoLessThanDuration)
                else -> it
            }
        }
    }

    private fun onToggleLessThanLength() {
        PreferenceUtil.filterVideoLessThanSize = !PreferenceUtil.filterVideoLessThanSize
        _settingItems.value = _settingItems.value.orEmpty().map {
            when (it) {
                is FilterAudioSettingsItem -> it.copy(filterLessThanSize = PreferenceUtil.filterVideoLessThanSize)
                else -> it
            }
        }
    }
}