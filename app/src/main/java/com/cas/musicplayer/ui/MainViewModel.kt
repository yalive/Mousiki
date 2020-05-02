package com.cas.musicplayer.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cas.common.connectivity.ConnectivityState
import com.cas.common.event.Event
import com.cas.common.event.asEvent
import com.cas.common.viewmodel.BaseViewModel
import com.cas.musicplayer.data.config.RemoteAppConfig
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.player.VideoEmplacement
import com.cas.musicplayer.ui.common.PlaySongDelegate
import com.cas.musicplayer.utils.UserPrefs
import com.cas.musicplayer.utils.uiCoroutine
import kotlinx.coroutines.delay
import javax.inject.Inject

/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class MainViewModel @Inject constructor(
    private val remoteAppConfig: RemoteAppConfig,
    val connectivityState: ConnectivityState,
    delegate: PlaySongDelegate
) : BaseViewModel(), PlaySongDelegate by delegate {

    var lastVideoEmplacement: VideoEmplacement? = null

    private val _rateApp = MutableLiveData<Event<Unit>>()
    val rateApp: LiveData<Event<Unit>>
        get() = _rateApp

    fun playTrackFromDeepLink(track: MusicTrack) = uiCoroutine {
        playTrackFromQueue(track, listOf(track))
    }

    fun checkToRateApp() = uiCoroutine {
        val launchCount = UserPrefs.getLaunchCount()
        val frequencyRateAppPopup = remoteAppConfig.getFrequencyRateAppPopup()
        if (!UserPrefs.hasRatedApp() && launchCount % frequencyRateAppPopup == 0) {
            delay(500)
            _rateApp.value = Unit.asEvent()
        }
    }
}