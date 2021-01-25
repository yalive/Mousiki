package com.cas.musicplayer.player.services

sealed class PlayerCommand {
    object PlayTrack : PlayerCommand()
    object Resume : PlayerCommand()
    object Pause : PlayerCommand()
    data class SeekTo(val seconds: Long) : PlayerCommand()
    data class ScheduleTimer(val duration: Int) : PlayerCommand()
}