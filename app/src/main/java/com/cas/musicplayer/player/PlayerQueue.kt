package com.cas.musicplayer.player

import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.lifecycle.MutableLiveData
import com.cas.common.extensions.randomOrNull
import com.cas.musicplayer.MusicApp
import com.cas.musicplayer.R
import com.mousiki.shared.domain.models.MusicTrack
import com.cas.musicplayer.player.services.MusicPlayerService
import com.cas.musicplayer.player.services.PlaybackLiveData
import com.cas.musicplayer.ui.popular.swapped
import com.cas.musicplayer.utils.UserPrefs
import com.cas.musicplayer.utils.canDrawOverApps


/**
 **********************************
 * Created by Abdelhadi on 4/13/19.
 **********************************
 */

object PlayerQueue : MutableLiveData<MusicTrack>() {

    var queue: List<MusicTrack>? = null

    fun playTrack(currentTrack: MusicTrack, queue: List<MusicTrack>) {
        if (value == currentTrack) {
            resume()
            return
        }
        val oldQueue = this.queue.orEmpty()
        this.queue = queue
        this.value = currentTrack
        notifyService(currentTrack.youtubeId)
        checkQueueChanged(oldQueue, queue)
    }

    fun playNextTrack() {
        val nextTrack = getNextTrack()
        if (nextTrack != null) {
            this.value = nextTrack
            notifyService(nextTrack.youtubeId)
        }
    }

    fun playPreviousTrack() {
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

            val oldQueue = queue.orEmpty()

            this.queue = newList
            checkQueueChanged(oldQueue, newList)
        }
    }

    fun removeTrack(track: MusicTrack) {
        val mutableQueue = queue?.toMutableList()
        mutableQueue?.remove(track)
        this.queue = mutableQueue
    }

    fun swapTracks(from: Int, to: Int) {
        val swappedQueue = queue?.swapped(from, to)
        queue = swappedQueue
    }

    fun togglePlayback() {
        if (PlaybackLiveData.isPlaying()) {
            pause()
        } else {
            resume()
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

    private fun getNextTrack(): MusicTrack? {
        val mQueue = queue ?: return null
        if (mQueue.isEmpty()) return null
        val currentTrack = this.value ?: return null
        return when (getPlaySort()) {
            PlaySort.SEQUENCE -> mQueue.getOrNull(mQueue.indexOf(currentTrack) + 1)
            PlaySort.LOOP_ONE -> currentTrack
            PlaySort.LOOP_ALL -> mQueue.getOrElse(mQueue.indexOf(currentTrack) + 1) { mQueue[0] }
            PlaySort.RANDOM -> mQueue.filter { it != currentTrack }.randomOrNull()
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
            PlaySort.RANDOM -> mQueue.filter { it != currentTrack }.randomOrNull()
        }
    }

    private fun getPlaySort(): PlaySort {
        return UserPrefs.getSort()
    }

    private fun notifyService(videoId: String) {
        if (!MusicApp.get().canDrawOverApps()) {
            return
        }
        val intent = Intent(MusicApp.get(), MusicPlayerService::class.java)
        intent.putExtra(MusicPlayerService.COMMAND_PLAY_TRACK, true)
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
        val intent = Intent(MusicApp.get(), MusicPlayerService::class.java)
        intent.putExtra(MusicPlayerService.COMMAND_RESUME, true)
        MusicApp.get().startService(intent)
    }

    private fun seekTrackTo(to: Long) {
        if (!MusicApp.get().canDrawOverApps()) {
            return
        }

        val intent = Intent(MusicApp.get(), MusicPlayerService::class.java)
        intent.putExtra(MusicPlayerService.COMMAND_SEEK_TO, to)
        MusicApp.get().startService(intent)
    }

    fun size() = queue?.size ?: 0

    fun indexOfCurrent(): Int {
        val currentTrack = value ?: return -1
        return queue?.indexOf(currentTrack) ?: -1
    }

    fun playTrackAt(position: Int) {
        val currentQueue = queue ?: emptyList()
        val track = currentQueue.getOrNull(position) ?: return
        playTrack(track, currentQueue)
    }

    private fun checkQueueChanged(oldQueue: List<MusicTrack>, newQueue: List<MusicTrack>) {
        if (oldQueue.size != newQueue.size) {
            OnChangeQueue.value = newQueue
            return
        }

        if (oldQueue != newQueue) {
            OnChangeQueue.value = newQueue
        }
    }
}

object OnChangeQueue : MutableLiveData<List<MusicTrack>>()

enum class PlaySort(@DrawableRes val icon: Int) {
    RANDOM(R.drawable.ic_random),
    LOOP_ONE(R.drawable.ic_repeat_one),
    LOOP_ALL(R.drawable.ic_repeat),
    SEQUENCE(R.drawable.ic_arrow_alt_to_right);

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
                LOOP_ALL
            }
        }
    }
}
