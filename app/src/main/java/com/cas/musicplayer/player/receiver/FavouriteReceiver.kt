package com.cas.musicplayer.player.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import com.cas.musicplayer.player.services.MusicPlayerService.Companion.CustomAction.ADD_TO_FAVOURITE
import com.cas.musicplayer.player.services.MusicPlayerService.Companion.CustomAction.REMOVE_FROM_FAVOURITE

class FavouriteReceiver(
    private val context: Context,
    sessionToken: MediaSessionCompat.Token
) : BroadcastReceiver() {
    private val mediaController = MediaControllerCompat(context, sessionToken)
    private val favouriteIntentFilter = IntentFilter(ACTION_FAVOURITE)
    private var registered = false
    override fun onReceive(context: Context?, receivedIntent: Intent?) {
        if (receivedIntent?.action == ACTION_FAVOURITE) {
            val addToFav = receivedIntent.getBooleanExtra(EXTRAS_ADD_TO_FAVOURITE, false)
            val action = if (addToFav) ADD_TO_FAVOURITE
            else REMOVE_FROM_FAVOURITE
            mediaController.transportControls.sendCustomAction(action, null)
        }
    }

    fun register() {
        if (!registered) {
            context.registerReceiver(this, favouriteIntentFilter)
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
        const val ACTION_FAVOURITE = "com.mousik.media.favourite"
        const val EXTRAS_ADD_TO_FAVOURITE = "extras-add-to-favourite"

        fun broadcast(context: Context, clickAdd: Boolean) {
            val intentFav = Intent(ACTION_FAVOURITE)
            intentFav.putExtra(EXTRAS_ADD_TO_FAVOURITE, clickAdd)
            context.sendBroadcast(intentFav)
        }
    }
}