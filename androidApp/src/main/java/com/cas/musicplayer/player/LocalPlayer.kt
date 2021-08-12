package com.cas.musicplayer.player

import android.content.Context
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import com.cas.musicplayer.player.services.PlaybackDuration
import com.cas.musicplayer.player.services.PlaybackLiveData
import com.cas.musicplayer.ui.local.repository.LocalSongsRepository
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.audio.AudioAttributes
import com.mousiki.shared.domain.models.LocalSong
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import kotlinx.coroutines.*


class LocalPlayer(
    private val context: Context,
    private val scope: CoroutineScope,
    private val mediaSessionHandler: MediaSessionHandler,
    private val localSongsRepository: LocalSongsRepository
) : MousikiPlayer, MediaSessionHandler by mediaSessionHandler {

    private var playbackProgressJob: Job? = null
    private val exoPlayer by lazy {
        SimpleExoPlayer.Builder(context).build().apply {
            prepare()
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    Log.d(TAG_PLAYER, "Local player onPlaybackStateChanged ${stateToString(state)}")
                    PlaybackLiveData.value = when (state) {
                        Player.STATE_IDLE -> PlayerConstants.PlayerState.UNSTARTED
                        Player.STATE_ENDED -> PlayerConstants.PlayerState.ENDED
                        Player.STATE_READY, Player.STATE_BUFFERING -> {
                            Log.d(
                                TAG_PLAYER,
                                "Local player onPlaybackStateChanged to ready and isPlaying = $isPlaying"
                            )
                            if (isPlaying) PlayerConstants.PlayerState.PLAYING else PlayerConstants.PlayerState.PAUSED
                        }
                        else -> PlayerConstants.PlayerState.UNKNOWN
                    }
                    updateMediaSessionState(state)

                    if (state == Player.STATE_ENDED) {
                        PlayerQueue.playNextTrack()
                    }
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    Log.d(TAG_PLAYER, "Local player onIsPlayingChanged $isPlaying")
                    PlaybackLiveData.value = when (isPlaying) {
                        true -> PlayerConstants.PlayerState.PLAYING
                        else -> PlayerConstants.PlayerState.PAUSED
                    }
                    val state = if (isPlaying) Player.STATE_READY else Player.STATE_ENDED
                    updateMediaSessionState(state)
                    if (isPlaying) updatePlaybackProgress() else playbackProgressJob?.cancel()
                }
            })

            val audioAttributes = AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.CONTENT_TYPE_SPEECH)
                .build()
            setAudioAttributes(audioAttributes, true)
        }
    }

    override fun loadVideo(videoId: String, startSeconds: Float) {
        Log.d(TAG_PLAYER, "Local player loadVideo $videoId")
        val track = PlayerQueue.getTrack(videoId) ?: return
        if (track !is LocalSong) return
        scope.launch {
            val data = localSongsRepository.song(videoId.toLong()).data
            exoPlayer.setMediaItem(MediaItem.fromUri(data))
            exoPlayer.playWhenReady = true
        }
    }

    override fun cueVideo(videoId: String, startSeconds: Float) {
        Log.d(TAG_PLAYER, "Local player cueVideo $videoId")
    }

    override fun play() {
        Log.d(TAG_PLAYER, "Local player play")
        exoPlayer.play()
    }

    override fun pause() {
        Log.d(TAG_PLAYER, "Local player pause")
        exoPlayer.pause()
    }

    override fun stop() {
        Log.d(TAG_PLAYER, "Local player stop")
        exoPlayer.stop()
    }

    override fun seekTo(time: Float) {
        Log.d(TAG_PLAYER, "Local player seekTo $time")
        exoPlayer.seekTo(time.toLong() * 1000)
        PlaybackDuration.value = time.toInt()
    }

    private fun updatePlaybackProgress() {
        playbackProgressJob?.cancel()
        playbackProgressJob = scope.launch {
            while (isActive && exoPlayer.isPlaying) {
                val currentPosition = exoPlayer.currentPosition.toInt() / 1000
                PlaybackDuration.value = currentPosition
                delay(500)
            }
        }
    }

    private fun updateMediaSessionState(state: Int) {
        val playbackState = when (state) {
            Player.STATE_BUFFERING -> PlaybackStateCompat.STATE_PLAYING
            Player.STATE_READY -> if (exoPlayer.isPlaying) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED
            else -> PlaybackStateCompat.STATE_PAUSED
        }
        setMediaPlaybackState(playbackState, exoPlayer.currentPosition)
    }

    private fun stateToString(state: Int): String {
        return when (state) {
            Player.STATE_IDLE -> "IDLE"
            Player.STATE_READY -> "READY"
            Player.STATE_BUFFERING -> "BUFFERING"
            Player.STATE_ENDED -> "ENDED"
            else -> "NONE"
        }
    }
}