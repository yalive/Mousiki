package com.cas.musicplayer.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cas.common.event.Event
import com.cas.common.event.asEvent
import com.cas.common.viewmodel.BaseViewModel
import com.cas.musicplayer.BuildConfig
import com.cas.musicplayer.ui.common.PlaySongDelegate
import com.cas.musicplayer.utils.UserPrefs
import com.cas.musicplayer.utils.uiCoroutine
import com.mousiki.shared.data.config.RemoteAppConfig
import com.mousiki.shared.domain.models.MusicTrack
import com.mousiki.shared.utils.AnalyticsApi
import kotlinx.coroutines.delay
import javax.inject.Inject

/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class MainViewModel @Inject constructor(
    private val remoteAppConfig: RemoteAppConfig,
    private val analytics: AnalyticsApi,
    delegate: PlaySongDelegate
) : BaseViewModel(), PlaySongDelegate by delegate {

    private val _rateApp = MutableLiveData<Event<Unit>>()
    val rateApp: LiveData<Event<Unit>>
        get() = _rateApp

    private val _doubleClickSearch = MutableLiveData<Event<Unit>>()
    val doubleClickSearch: LiveData<Event<Unit>>
        get() = _doubleClickSearch

    fun playTrackFromDeepLink(track: MusicTrack) = uiCoroutine {
        playTrackFromQueue(track, listOf(track))
    }

    fun playTrackFromPushNotification(track: MusicTrack) = uiCoroutine {
        playTrackFromQueue(track, listOf(track))
    }

    fun checkToRateApp() = uiCoroutine {
        if (BuildConfig.DEBUG) return@uiCoroutine
        val launchCount = UserPrefs.getLaunchCount()
        val frequencyRateAppPopup = remoteAppConfig.getFrequencyRateAppPopup()
        if (!UserPrefs.hasRatedApp() && launchCount % frequencyRateAppPopup == 0) {
            delay(500)
            _rateApp.value = Unit.asEvent()
        }
    }

    fun onDoubleClickSearchNavigation() {
        _doubleClickSearch.value = Unit.asEvent()
    }

    fun checkStartFromShortcut(data: String?) {
        if (data == DEEP_LINK_SEARCH) {
            analytics.logEvent(EVENT_OPEN_APP_SHORTCUT_SEARCH)
        } else if (data == DEEP_LINK_TRENDING) {
            analytics.logEvent(EVENT_OPEN_APP_SHORTCUT_TRENDING)
        }
    }

    fun checkForUpdate() {

    }

    companion object {
        private const val EVENT_OPEN_APP_SHORTCUT_SEARCH = "open_app_from_search_shortcut"
        private const val EVENT_OPEN_APP_SHORTCUT_TRENDING = "open_app_from_trending_shortcut"

        const val DEEP_LINK_SEARCH = "mousiki://start_search"
        const val DEEP_LINK_TRENDING = "mousiki://open_new_releases"
    }
}