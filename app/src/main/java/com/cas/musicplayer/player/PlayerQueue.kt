package com.cas.musicplayer.player

import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.lifecycle.MutableLiveData
import com.cas.musicplayer.MusicApp
import com.cas.musicplayer.R
import com.cas.common.event.Event
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.player.services.VideoPlaybackService
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

    fun seekTo(to: Long, comeFromFullScreen: Boolean = false) {
        seekTrackTo(to, comeFromFullScreen)
    }

    fun hideVideo() {
        val intent = Intent(MusicApp.get(), VideoPlaybackService::class.java)
        intent.putExtra(VideoPlaybackService.COMMAND_HIDE_VIDEO, true)
        MusicApp.get().startService(intent)
    }

    fun showVideo() {
        val intent = Intent(MusicApp.get(), VideoPlaybackService::class.java)
        intent.putExtra(VideoPlaybackService.COMMAND_SHOW_VIDEO, true)
        MusicApp.get().startService(intent)
    }

    fun getNextTrack(): MusicTrack? {
        if (queue == null) {
            return null
        }

        val mValue = this.value ?: return null
        val mQueue = queue!!
        return when (getPlaySort()) {
            PlaySort.SEQUENCE -> mQueue.getOrNull(mQueue.indexOf(mValue) + 1)
            PlaySort.LOOP_ONE -> mValue
            PlaySort.LOOP_ALL -> mQueue.getOrElse(mQueue.indexOf(mValue) + 1) { mQueue[0] }
            PlaySort.RANDOM -> mQueue.filter { it != mValue }.random()
        }
    }

    private fun getPreviousTrack(): MusicTrack? {
        if (queue == null) {
            return null
        }
        val mValue = this.value ?: return null
        val mQueue = queue!!
        return when (getPlaySort()) {
            PlaySort.SEQUENCE -> mQueue.getOrNull(mQueue.indexOf(mValue) - 1)
            PlaySort.LOOP_ONE -> mValue
            PlaySort.LOOP_ALL -> mQueue.getOrElse(mQueue.indexOf(mValue) - 1) { mQueue[mQueue.size - 1] }
            PlaySort.RANDOM -> mQueue.filter { it != mValue }.random()
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
        val intent = Intent(MusicApp.get(), VideoPlaybackService::class.java)
        intent.putExtra(VideoPlaybackService.COMMAND_PLAY_TRACK, videoId)
        MusicApp.get().startService(intent)
    }

    private fun pauseVideo() {
        if (!MusicApp.get().canDrawOverApps()) {
            return
        }
        val intent = Intent(MusicApp.get(), VideoPlaybackService::class.java)
        intent.putExtra(VideoPlaybackService.COMMAND_PAUSE, true)
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
        val intent = Intent(MusicApp.get(), VideoPlaybackService::class.java)
        intent.putExtra(VideoPlaybackService.COMMAND_RESUME, true)
        MusicApp.get().startService(intent)
    }

    private fun seekTrackTo(to: Long, comeFromFullScreen: Boolean) {
        if (!MusicApp.get().canDrawOverApps()) {
            return
        }
        if (isScreenLocked()) {
            pauseVideo()
            return
        }
        val intent = Intent(MusicApp.get(), VideoPlaybackService::class.java)
        intent.putExtra(VideoPlaybackService.COMMAND_SEEK_TO, to)
        intent.putExtra(VideoPlaybackService.COMMAND_SEEK_TO_FROM_FULL_SCREEN, comeFromFullScreen)
        MusicApp.get().startService(intent)
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
