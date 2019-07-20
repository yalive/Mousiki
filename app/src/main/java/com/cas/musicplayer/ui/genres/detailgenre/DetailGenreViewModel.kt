package com.cas.musicplayer.ui.genres.detailgenre

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cas.musicplayer.data.enteties.MusicTrack
import javax.inject.Inject

/**
 **********************************
 * Created by Abdelhadi on 4/16/19.
 **********************************
 */
class DetailGenreViewModel @Inject constructor() : ViewModel() {

    val firstTrack = MutableLiveData<MusicTrack>()
}