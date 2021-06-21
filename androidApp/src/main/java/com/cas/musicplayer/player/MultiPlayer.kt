package com.cas.musicplayer.player

import android.util.Log
import com.mousiki.shared.domain.models.LocalSong
import com.mousiki.shared.domain.models.MusicTrack

const val TAG_PLAYER = "MultiPlayer"

class MultiPlayer(
    private val ytbPlayer: YTBPlayer,
    private val localPlayer: LocalPlayer
) : MousikiPlayer {

    private var currentPlayer: MousikiPlayer? = null

    override fun loadVideo(videoId: String, startSeconds: Float) {
        Log.d(TAG_PLAYER, "loadVideo: $videoId")
        val track = PlayerQueue.getTrack(videoId) ?: return
        when (track) {
            is LocalSong -> {
                currentPlayer = localPlayer
                ytbPlayer.pause()
                localPlayer.loadVideo(videoId, startSeconds)
            }
            is MusicTrack -> {
                currentPlayer = ytbPlayer
                localPlayer.pause()
                ytbPlayer.loadVideo(videoId, startSeconds)
            }
        }
    }

    override fun cueVideo(videoId: String, startSeconds: Float) {
        Log.d(TAG_PLAYER, "cueVideo: $videoId")
        val track = PlayerQueue.getTrack(videoId) ?: return
        when (track) {
            is LocalSong -> {
                currentPlayer = localPlayer
                ytbPlayer.pause()
                localPlayer.cueVideo(videoId, startSeconds)
            }
            is MusicTrack -> {
                currentPlayer = ytbPlayer
                localPlayer.pause()
                ytbPlayer.cueVideo(videoId, startSeconds)
            }
        }
    }

    override fun play() {
        Log.d(TAG_PLAYER, "play")
        currentPlayer?.play()
    }

    override fun pause() {
        Log.d(TAG_PLAYER, "pause")
        currentPlayer?.pause()
    }

    override fun stop() {
        Log.d(TAG_PLAYER, "stop")
        currentPlayer?.stop()
    }

    override fun seekTo(time: Float) {
        Log.d(TAG_PLAYER, "seekTo $time")
        currentPlayer?.seekTo(time)
    }
}