package com.cas.musicplayer.player

import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.lifecycle.MutableLiveData
import com.cas.common.event.Event
import com.cas.musicplayer.MusicApp
import com.cas.musicplayer.R
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.player.services.MusicPlayerService
import com.cas.musicplayer.player.services.PlaybackLiveData
import com.cas.musicplayer.utils.UserPrefs
import com.cas.musicplayer.utils.canDrawOverApps
import com.cas.musicplayer.utils.isScreenLocked


/**
 **********************************
 * Created by Abdelhadi on 4/13/19.
 **********************************
 */

object ClickVideoListener : MutableLiveData<Event<MusicTrack>>()

object OnShowAdsListener : MutableLiveData<Event<Boolean>>()

object PlayerQueue : MutableLiveData<MusicTrack>() {

    var queue: List<MusicTrack>? = null

    fun playTrack(currentTrack: MusicTrack, queue: List<MusicTrack>) {
        if (isScreenLocked()) return
        if (value == currentTrack) {
            resume()
            return
        }
        this.queue = queue
        this.value = currentTrack
        notifyService(currentTrack.youtubeId)

        // For Ads
        UserPrefs.onClickTrack()
        ClickVideoListener.value = Event(currentTrack)
    }

    fun playNextTrack() {
        if (isScreenLocked()) return
        val nextTrack = getNextTrack()
        if (nextTrack != null) {
            this.value = nextTrack
            notifyService(nextTrack.youtubeId)
        }
    }

    fun playPreviousTrack() {
        if (isScreenLocked()) return
        val previousTrack = getPreviousTrack()
        if (previousTrack != null) {
            this.value = previousTrack
            notifyService(previousTrack.youtubeId)
        }
    }

    fun addAsNext(track: MusicTrack) {

        val newList = mutableListOf<MusicTrack>()
        if (queue != null) {
            for (musicTrack in queue!!) {
                newList.add(musicTrack)
                if (musicTrack.youtubeId == value?.youtubeId) {
                    newList.add(track)
                }
            }

            this.queue = newList
        }
    }

    fun pause() {
        pauseVideo()
    }

    fun resume() {
        resumeVideo()
    }

    fun seekTo(to: Long) {
        seekTrackTo(to)
    }

    fun scheduleStopMusic(duration: Int) {
        if (!MusicApp.get().canDrawOverApps()) {
            return
        }
        if (value == null) return
        val intent = Intent(MusicApp.get(), MusicPlayerService::class.java)
        intent.putExtra(MusicPlayerService.COMMAND_SCHEDULE_TIMER, duration)
        MusicApp.get().startService(intent)
    }

    fun isCurrentTrack(musicTrack: MusicTrack): Boolean {
        val currentTrack = value ?: return false
        return currentTrack.youtubeId == musicTrack.youtubeId
    }

    fun hideVideo() {
        val intent = Intent(MusicApp.get(), MusicPlayerService::class.java)
        intent.putExtra(MusicPlayerService.COMMAND_HIDE_VIDEO, true)
        MusicApp.get().startService(intent)
    }

    fun showVideo() {
        val intent = Intent(MusicApp.get(), MusicPlayerService::class.java)
        intent.putExtra(MusicPlayerService.COMMAND_SHOW_VIDEO, true)
        MusicApp.get().startService(intent)
    }

    private fun getNextTrack(): MusicTrack? {
        val mQueue = queue ?: return null
        if (mQueue.isEmpty()) return null
        val currentTrack = this.value ?: return null
        return when (getPlaySort()) {
            PlaySort.SEQUENCE -> mQueue.getOrNull(mQueue.indexOf(currentTrack) + 1)
            PlaySort.LOOP_ONE -> currentTrack
            PlaySort.LOOP_ALL -> mQueue.getOrElse(mQueue.indexOf(currentTrack) + 1) { mQueue[0] }
            PlaySort.RANDOM -> mQueue.filter { it != currentTrack }.random()
        }
    }

    private fun getPreviousTrack(): MusicTrack? {
        val mQueue = queue ?: return null
        if (mQueue.isEmpty()) return null
        val currentTrack = this.value ?: return null
        return when (getPlaySort()) {
            PlaySort.SEQUENCE -> mQueue.getOrNull(mQueue.indexOf(currentTrack) - 1)
            PlaySort.LOOP_ONE -> currentTrack
            PlaySort.LOOP_ALL -> mQueue.getOrElse(mQueue.indexOf(currentTrack) - 1) { mQueue[mQueue.size - 1] }
            PlaySort.RANDOM -> mQueue.filter { it != currentTrack }.random()
        }
    }

    private fun getPlaySort(): PlaySort {
        return UserPrefs.getSort()
    }

    private fun notifyService(videoId: String) {
        if (!MusicApp.get().canDrawOverApps()) {
            return
        }
        if (isScreenLocked()) {
            pauseVideo()
            return
        }
        val intent = Intent(MusicApp.get(), MusicPlayerService::class.java)
        intent.putExtra(MusicPlayerService.COMMAND_PLAY_TRACK, videoId)
        MusicApp.get().startService(intent)
    }

    private fun pauseVideo() {
        if (!MusicApp.get().canDrawOverApps()) {
            return
        }
        val intent = Intent(MusicApp.get(), MusicPlayerService::class.java)
        intent.putExtra(MusicPlayerService.COMMAND_PAUSE, true)
        MusicApp.get().startService(intent)
    }

    private fun resumeVideo() {
        if (!MusicApp.get().canDrawOverApps()) {
            return
        }
        if (isScreenLocked()) {
            pauseVideo()
            return
        }
        val intent = Intent(MusicApp.get(), MusicPlayerService::class.java)
        intent.putExtra(MusicPlayerService.COMMAND_RESUME, true)
        MusicApp.get().startService(intent)
    }

    private fun seekTrackTo(to: Long) {
        if (!MusicApp.get().canDrawOverApps()) {
            return
        }
        if (isScreenLocked()) {
            pauseVideo()
            return
        }
        val intent = Intent(MusicApp.get(), MusicPlayerService::class.java)
        intent.putExtra(MusicPlayerService.COMMAND_SEEK_TO, to)
        MusicApp.get().startService(intent)
    }

    fun togglePlay() {
        if (PlaybackLiveData.isPlaying()) {
            pause()
        } else if (PlaybackLiveData.isPause()) {
            resume()
        }
    }
}

enum class PlaySort(@DrawableRes val icon: Int) {
    RANDOM(R.drawable.ic_shuffle),
    LOOP_ONE(R.drawable.ic_repeat_one),
    LOOP_ALL(R.drawable.ic_repeat_all),
    SEQUENCE(R.drawable.ic_sequence);

    fun next(): PlaySort = when {
        this == RANDOM -> LOOP_ONE
        this == LOOP_ONE -> LOOP_ALL
        this == LOOP_ALL -> SEQUENCE
        else -> RANDOM
    }

    companion object {
        fun toEnum(enumString: String): PlaySort {
            return try {
                valueOf(enumString)
            } catch (ex: Exception) {
                // For error cases
                SEQUENCE
            }
        }
    }
}
