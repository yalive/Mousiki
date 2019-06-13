package com.secureappinc.musicplayer.ui.genres.detailgenre

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.secureappinc.musicplayer.data.enteties.MusicTrack

/**
 **********************************
 * Created by Abdelhadi on 4/16/19.
 **********************************
 */
class DetailGenreViewModel : ViewModel() {

    val firstTrack = MutableLiveData<MusicTrack>()
}