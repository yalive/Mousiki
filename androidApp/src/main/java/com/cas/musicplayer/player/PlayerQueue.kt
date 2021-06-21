package com.cas.musicplayer.player

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.cas.common.extensions.randomOrNull
import com.cas.musicplayer.MusicApp
import com.cas.musicplayer.player.services.MusicPlayerService
import com.cas.musicplayer.player.services.PlaybackLiveData
import com.cas.musicplayer.utils.canDrawOverApps
import com.mousiki.shared.domain.models.Track
import com.mousiki.shared.player.PlaySort
import com.mousiki.shared.preference.UserPrefs
import com.mousiki.shared.utils.swapped


/**
 **********************************
 * Created by Abdelhadi on 4/13/19.
 **********************************
 */

object PlayerQueue : MutableLiveData<Track>() {

    var queue: List<Track>? = null

    fun playTrack(currentTrack: Track, queue: List<Track>) {
        if (value == currentTrack) {
            resume()
            return
        }
        val oldQueue = this.queue.orEmpty()
        this.queue = queue
        this.value = currentTrack
        playCurrentTrack()
        checkQueueChanged(oldQueue, queue)
    }

    fun cueTrack(currentTrack: Track, queue: List<Track>) {
        this.queue = queue
        this.value = currentTrack
        cueTrack()
        OnChangeQueue.value = queue
    }

    fun playNextTrack() {
        val nextTrack = getNextTrack()
        if (nextTrack != null) {
            this.value = nextTrack
            playCurrentTrack()
        }
    }

    fun playPreviousTrack() {
        val previousTrack = getPreviousTrack()
        if (previousTrack != null) {
            this.value = previousTrack
            playCurrentTrack()
        }
    }

    fun addAsNext(track: Track) {
        val newList = mutableListOf<Track>()
        if (queue != null) {
            for (musicTrack in queue!!) {
                newList.add(musicTrack)
                if (musicTrack.id == value?.id) {
                    newList.add(track)
                }
            }

            val oldQueue = queue.orEmpty()

            this.queue = newList
            checkQueueChanged(oldQueue, newList)
        }
    }

    fun removeTrack(track: Track) {
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
        if (!MusicApp.get().canDrawOverApps()) {
            return
        }
        val intent = Intent(MusicApp.get(), MusicPlayerService::class.java)
        intent.putExtra(MusicPlayerService.COMMAND_PAUSE, true)
        MusicApp.get().startService(intent)
    }

    fun resume() {
        if (!MusicApp.get().canDrawOverApps()) {
            return
        }
        val intent = Intent(MusicApp.get(), MusicPlayerService::class.java)
        intent.putExtra(MusicPlayerService.COMMAND_RESUME, true)
        MusicApp.get().startService(intent)
    }

    fun seekTo(to: Long) {
        if (!MusicApp.get().canDrawOverApps()) {
            return
        }

        val intent = Intent(MusicApp.get(), MusicPlayerService::class.java)
        intent.putExtra(MusicPlayerService.COMMAND_SEEK_TO, to)
        MusicApp.get().startService(intent)
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

    fun isCurrentTrack(musicTrack: Track): Boolean {
        val currentTrack = value ?: return false
        return currentTrack.id == musicTrack.id
    }

    private fun getNextTrack(): Track? {
        val mQueue = queue ?: return null
        if (mQueue.isEmpty()) return null
        val currentTrack = this.value ?: return null
        return when (UserPrefs.getCurrentPlaybackSort()) {
            PlaySort.SEQUENCE -> mQueue.getOrNull(mQueue.indexOf(currentTrack) + 1)
            PlaySort.LOOP_ONE -> currentTrack
            PlaySort.LOOP_ALL -> mQueue.getOrElse(mQueue.indexOf(currentTrack) + 1) { mQueue[0] }
            PlaySort.RANDOM -> mQueue.filter { it != currentTrack }.randomOrNull()
        }
    }

    private fun getPreviousTrack(): Track? {
        val mQueue = queue ?: return null
        if (mQueue.isEmpty()) return null
        val currentTrack = this.value ?: return null
        return when (UserPrefs.getCurrentPlaybackSort()) {
            PlaySort.SEQUENCE -> mQueue.getOrNull(mQueue.indexOf(currentTrack) - 1)
            PlaySort.LOOP_ONE -> currentTrack
            PlaySort.LOOP_ALL -> mQueue.getOrElse(mQueue.indexOf(currentTrack) - 1) { mQueue[mQueue.size - 1] }
            PlaySort.RANDOM -> mQueue.filter { it != currentTrack }.randomOrNull()
        }
    }

    fun playCurrentTrack() {
        if (!MusicApp.get().canDrawOverApps()) {
            return
        }
        val intent = Intent(MusicApp.get(), MusicPlayerService::class.java)
        intent.putExtra(MusicPlayerService.COMMAND_PLAY, true)
        MusicApp.get().startService(intent)
    }

    private fun cueTrack() {
        if (!MusicApp.get().canDrawOverApps()) {
            return
        }
        val intent = Intent(MusicApp.get(), MusicPlayerService::class.java)
        intent.putExtra(MusicPlayerService.COMMAND_CUE, true)
        MusicApp.get().startService(intent)
    }

    private fun checkQueueChanged(oldQueue: List<Track>, newQueue: List<Track>) {
        if (oldQueue.size != newQueue.size) {
            OnChangeQueue.value = newQueue
            return
        }
        if (oldQueue != newQueue) {
            OnChangeQueue.value = newQueue
        }
    }
}

fun PlayerQueue.getTrack(id: String): Track? {
    return queue.orEmpty().firstOrNull { it.id == id }
}

object OnChangeQueue : MutableLiveData<List<Track>>()

