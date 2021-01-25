package com.cas.musicplayer.player.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat

class DeleteNotificationReceiver(
    private val context: Context,
    sessionToken: MediaSessionCompat.Token
) : BroadcastReceiver() {

    private val intentFilter = IntentFilter(ACTION_DELETE_NOTIFICATION)
    private var registered = false
    private val mediaController = MediaControllerCompat(context, sessionToken)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_DELETE_NOTIFICATION) {
            mediaController.transportControls?.stop()
        }
    }

    fun register() {
        if (!registered) {
            context.registerReceiver(this, intentFilter)
            registered = true
        }
    }

    fun unregister() {
        if (registered) {
            context.unregisterReceiver(this)
            registered = false
        }
    }

    companion object {
        const val ACTION_DELETE_NOTIFICATION = "mousiki.action.delete.notification"
    }
}