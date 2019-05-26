package com.secureappinc.musicplayer.utils

import androidx.lifecycle.MutableLiveData

/**
 **********************************
 * Created by Abdelhadi on 2019-05-20.
 **********************************
 */
object RequestAdsLiveData : MutableLiveData<AdsOrigin>()

data class AdsOrigin(val origin: String)