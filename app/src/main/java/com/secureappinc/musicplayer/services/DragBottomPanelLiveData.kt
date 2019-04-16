package com.secureappinc.musicplayer.services

import androidx.lifecycle.MutableLiveData

/**
 **********************************
 * Created by Abdelhadi on 4/10/19.
 **********************************
 */

data class DragPanelInfo(val pannelY: Float, val slideOffset: Float)

object DragBottomPanelLiveData : MutableLiveData<DragPanelInfo>()
object DragSlidePanelMonitor : MutableLiveData<Float>()
object DragBottomSheetMonitor : MutableLiveData<Int>()