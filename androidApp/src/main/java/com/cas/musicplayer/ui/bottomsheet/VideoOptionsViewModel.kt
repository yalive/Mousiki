package com.cas.musicplayer.ui.bottomsheet

import androidx.lifecycle.viewModelScope
import com.mousiki.shared.domain.models.Track
import com.mousiki.shared.domain.usecase.library.RemoveVideoFromRecentlyPlayedUseCase
import com.mousiki.shared.ui.base.BaseViewModel
import kotlinx.coroutines.launch


class VideoOptionsViewModel(
    private val removeFromRecentlyPlayed: RemoveVideoFromRecentlyPlayedUseCase
) : BaseViewModel() {

    fun removeVideoFromRecentlyPlayed(ytbTrack: Track) = viewModelScope.launch {
        removeFromRecentlyPlayed(ytbTrack.id)
    }
}