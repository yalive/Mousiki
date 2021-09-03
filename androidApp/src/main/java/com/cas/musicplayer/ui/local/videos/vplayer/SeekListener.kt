package com.cas.musicplayer.ui.local.videos.vplayer

interface SeekListener {
    /**
     * Called when video start reached during rewinding
     */
    fun onVideoStartReached()

    /**
     * Called when video end reached during forwarding
     */
    fun onVideoEndReached()
}