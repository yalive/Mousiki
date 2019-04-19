package com.secureappinc.musicplayer.ui.artistdetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.secureappinc.musicplayer.models.enteties.MusicTrack

/**
 **********************************
 * Created by Abdelhadi on 4/16/19.
 **********************************
 */
class ArtistViewModel : ViewModel() {

    val firstTrack = MutableLiveData<MusicTrack>()
}