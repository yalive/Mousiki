package com.secureappinc.musicplayer.player

import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.lifecycle.MutableLiveData
import com.secureappinc.musicplayer.MusicApp
import com.secureappinc.musicplayer.R
import com.secureappinc.musicplayer.models.enteties.MusicTrack
import com.secureappinc.musicplayer.services.VideoPlaybackService
import com.secureappinc.musicplayer.utils.UserPrefs


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

    fun repeatCurrentTrack() {
        value?.youtubeId?.let {
            notifyService(it)
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
        } else if (sort == PlaySort.LOOP_ONE) {
            return value
        } else if (sort == PlaySort.LOOP_ALL) {

            for ((index, track) in mQueue.withIndex()) {
                if (track == this.value && index < mQueue.size - 1) {
                    return mQueue[index + 1]
                } else if (track == this.value && index == mQueue.size - 1) {
                    return mQueue[0]
                }
            }
        } else if (sort == PlaySort.RANDOM) {

            val indexOfCurrent = mQueue.indexOf(value)

            var random = (0 until mQueue.size).random()

            while (indexOfCurrent == random) {
                random = (0 until mQueue.size).random()
            }

            return mQueue[random]
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
        } else if (sort == PlaySort.LOOP_ONE) {
            return value
        } else if (sort == PlaySort.LOOP_ALL) {
            for ((index, track) in mQueue.withIndex()) {
                if (track == this.value && index > 0) {
                    return mQueue[index - 1]
                } else if (track == this.value && index == 0) {
                    return mQueue[mQueue.size - 1]
                }
            }
        } else if (sort == PlaySort.RANDOM) {

            val indexOfCurrent = mQueue.indexOf(value)

            var random = (0 until mQueue.size).random()

            while (indexOfCurrent == random) {
                random = (0 until mQueue.size).random()
            }

            return mQueue[random]
        }

        return null
    }


    private fun getPlaySort(): PlaySort {
        return UserPrefs.getSort()
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

enum class PlaySort(@DrawableRes val icon: Int) {
    RANDOM(R.drawable.ic_shuffle),
    LOOP_ONE(R.drawable.ic_repeat_one),
    LOOP_ALL(R.drawable.ic_repeat_all),
    SEQUENCE(R.drawable.ic_sequence);

    fun next(): PlaySort {
        if (this == RANDOM) {
            return LOOP_ONE
        } else if (this == LOOP_ONE) {
            return LOOP_ALL
        } else if (this == LOOP_ALL) {
            return SEQUENCE
        } else {
            return RANDOM
        }
    }

    companion object {
        fun toEnum(enumString: String): PlaySort {
            return try {
                PlaySort.valueOf(enumString)
            } catch (ex: Exception) {
                // For error cases
                SEQUENCE
            }
        }
    }
}
