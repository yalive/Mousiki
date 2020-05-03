package com.cas.musicplayer.ui

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cas.common.connectivity.ConnectivityState
import com.cas.common.event.Event
import com.cas.common.event.asEvent
import com.cas.common.result.Result
import com.cas.common.viewmodel.BaseViewModel
import com.cas.musicplayer.BuildConfig
import com.cas.musicplayer.data.config.RemoteAppConfig
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.domain.usecase.artist.GetAllCountryArtistsUseCase
import com.cas.musicplayer.domain.usecase.artist.GetArtistSongsUseCase
import com.cas.musicplayer.player.VideoEmplacement
import com.cas.musicplayer.ui.common.PlaySongDelegate
import com.cas.musicplayer.utils.UserPrefs
import com.cas.musicplayer.utils.Utils
import com.cas.musicplayer.utils.uiCoroutine
import com.google.gson.Gson
import kotlinx.coroutines.delay
import java.io.File
import javax.inject.Inject

/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class MainViewModel @Inject constructor(
    private val remoteAppConfig: RemoteAppConfig,
    private val getAllCountryArtistsUseCase: GetAllCountryArtistsUseCase,
    private val getArtistSongsUseCase: GetArtistSongsUseCase,
    private val gson: Gson,
    val connectivityState: ConnectivityState,
    private val context: Context,
    delegate: PlaySongDelegate
) : BaseViewModel(), PlaySongDelegate by delegate {

    var lastVideoEmplacement: VideoEmplacement? = null

    private val _rateApp = MutableLiveData<Event<Unit>>()
    val rateApp: LiveData<Event<Unit>>
        get() = _rateApp

    init {
        //loadArtistsSongs()
    }

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

    fun loadArtistsSongs() = uiCoroutine {
        if (!BuildConfig.DEBUG) return@uiCoroutine
        val artists =
            getAllCountryArtistsUseCase("MA")

        println()
        for (i in 0 until artists.size) {
            val artist = artists[i]
            val tracks = getArtistSongsUseCase(artist)
            if (tracks is Result.Success) {
                val json = gson.toJson(tracks.data)
                val file = artistSongsFile(context, artist.channelId)
                Utils.writeToFile(json, file)
                println()
                Log.d("get_data", "Got data for $i/${artists.size - 1}")
            } else {
                Log.d("get_data", "Error get data for $i/${artists.size - 1} ($artist)")
                println("")
            }
        }
        Log.d("get_data", "Finished")
    }
}


private fun artistSongsFile(context: Context, channelId: String): File {
    val fileName = "$channelId.json"
    val fileDirPath =
        context.filesDir.absolutePath + File.separator + "artistsTracks" + File.separator
    val directory = File(fileDirPath)
    if (!directory.exists()) directory.mkdirs()
    return File(fileDirPath, fileName)
}