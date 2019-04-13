package com.secureappinc.musicplayer.player

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.secureappinc.musicplayer.MusicApp
import com.secureappinc.musicplayer.models.enteties.MusicTrack
import com.secureappinc.musicplayer.services.VideoPlaybackService

/**
 **********************************
 * Created by Abdelhadi on 4/13/19.
 **********************************
 */
object PlayerQueue : MutableLiveData<MusicTrack>() {

    var queue: List<MusicTrack>? = null


    fun playTrack(currentTrack: MusicTrack, queue: List<MusicTrack>) {
        this.queue = queue
        this.value = currentTrack
        notifyService(currentTrack.youtubeId)
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

    fun pause() {
        pauseVideo()
    }

    fun resume() {
        resumeVideo()
    }


    fun seekTo(to: Long) {
        seekTrackTo(to)
    }

    private fun getNextTrack(): MusicTrack? {

        if (queue == null) {
            return null
        }

        if (this.value == null) {
            return null
        }

        val mQueue = queue!!
        val sort = getPlaySort()
        if (sort == PlaySort.SEQUENCE) {
            for ((index, track) in mQueue.withIndex()) {
                if (track == this.value && index < mQueue.size - 1) {
                    return mQueue[index + 1]
                }
            }
        }

        return null
    }

    private fun getPreviousTrack(): MusicTrack? {

        if (queue == null) {
            return null
        }

        if (this.value == null) {
            return null
        }

        val mQueue = queue!!
        val sort = getPlaySort()
        if (sort == PlaySort.SEQUENCE) {
            for ((index, track) in mQueue.withIndex()) {
                if (track == this.value && index > 0) {
                    return mQueue[index - 1]
                }
            }
        }

        return null
    }


    private fun getPlaySort(): PlaySort {
        return PlaySort.SEQUENCE
    }


    private fun notifyService(videoId: String) {
        val intent = Intent(MusicApp.get(), VideoPlaybackService::class.java)
        intent.putExtra(VideoPlaybackService.COMMAND_PLAY_TRACK, videoId)
        MusicApp.get().startService(intent)
    }

    private fun pauseVideo() {
        val intent = Intent(MusicApp.get(), VideoPlaybackService::class.java)
        intent.putExtra(VideoPlaybackService.COMMAND_PAUSE, true)
        MusicApp.get().startService(intent)
    }

    private fun resumeVideo() {
        val intent = Intent(MusicApp.get(), VideoPlaybackService::class.java)
        intent.putExtra(VideoPlaybackService.COMMAND_RESUME, true)
        MusicApp.get().startService(intent)
    }

    private fun seekTrackTo(to: Long) {
        val intent = Intent(MusicApp.get(), VideoPlaybackService::class.java)
        intent.putExtra(VideoPlaybackService.COMMAND_SEEK_TO, to)
        MusicApp.get().startService(intent)
    }
}

enum class PlaySort {
    RANDOM,
    LOOP_ONE,
    LOOP_ALL,
    SEQUENCE
}
