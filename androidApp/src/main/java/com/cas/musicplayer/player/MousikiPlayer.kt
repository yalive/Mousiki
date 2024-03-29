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

    /**
     * Loads the video's thumbnail and prepares the player to play the video. Does not automatically play the video.
     * @param videoId id of the video
     * @param startSeconds the time from which the video should start playing
     */
    fun cueVideo(videoId: String, startSeconds: Float = 0f)

    fun play()

    fun pause()

    fun stop()

    /**
     *
     * @param time The absolute time in seconds to seek to
     */
    fun seekTo(time: Float)
}