package com.cas.musicplayer.player

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/22/20.
 ***************************************
 */
interface MousikiPlayer {

    /**
     * Loads and automatically plays the video.
     * @param videoId id of the video
     * @param startSeconds the time from which the video should start playing
     */
    fun loadVideo(videoId: String, startSeconds: Float)

    fun play()

    fun pause()

    /**
     *
     * @param time The absolute time in seconds to seek to
     */
    fun seekTo(time: Float)
}