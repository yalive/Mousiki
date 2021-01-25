package com.cas.musicplayer.player.services

import android.content.Intent
import android.os.SystemClock
import com.cas.musicplayer.MusicApp
import java.util.*

/**
 ***************************************
 * Created by Y.Abdelhadi on 3/21/20.
 ***************************************
 */
interface SleepTimer {
    companion object {
        const val ONE_SECOND = 1000
    }

    fun stopPlayer(afterDuration: Int)
}

class MusicSleepTimer : SleepTimer {
    private var stopTimer: Timer? = null
    override fun stopPlayer(afterDuration: Int) {
        // Cancel previous timer
        stopTimer?.cancel()

        val initialTime = SystemClock.elapsedRealtime()
        stopTimer = Timer()

        // Update the elapsed time every second.
        stopTimer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val elapsedSeconds = (SystemClock.elapsedRealtime() - initialTime) / 1000
                if (afterDuration * 60 - elapsedSeconds < 0) {
                    cancel() // Stop timer
                    val intent = Intent(MusicApp.get(), MusicPlayerService::class.java)
                    MusicApp.get().stopService(intent)
                }
            }
        }, SleepTimer.ONE_SECOND.toLong(), SleepTimer.ONE_SECOND.toLong())
    }
}