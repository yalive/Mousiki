package com.cas.musicplayer.ui.local.videos.player.views

interface PlayerDoubleTapListener {
    fun onDoubleTapStarted(posX: Float, posY: Float) {}
    fun onDoubleTapProgressDown(posX: Float, posY: Float) {}
    fun onDoubleTapProgressUp(posX: Float, posY: Float) {}
    fun onDoubleTapFinished() {}
}