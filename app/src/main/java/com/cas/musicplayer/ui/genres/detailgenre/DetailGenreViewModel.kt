package com.cas.musicplayer.ui.genres.detailgenre

import androidx.lifecycle.MutableLiveData
import com.cas.musicplayer.base.BaseViewModel
import com.cas.musicplayer.data.enteties.MusicTrack
import javax.inject.Inject

/**
 **********************************
 * Created by Abdelhadi on 4/16/19.
 **********************************
 */
class DetailGenreViewModel @Inject constructor() : BaseViewModel() {
    val firstTrack = MutableLiveData<MusicTrack>()
}