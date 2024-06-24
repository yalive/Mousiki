package com.cas.musicplayer.player.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.getActionButton
import com.cas.musicplayer.R
import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.player.TAG_PLAYER
import com.cas.musicplayer.player.extensions.isPlaying
import com.cas.musicplayer.ui.MainActivity
import com.cas.musicplayer.utils.*
import com.mousiki.shared.domain.models.AiTrack
import com.mousiki.shared.domain.models.LocalSong
import com.mousiki.shared.domain.models.YtbTrack

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/30/20.
 ***************************************
 */

class LockScreenReceiver(
    private val context: Context,
    sessionToken: MediaSessionCompat.Token
) : BroadcastReceiver() {

    private val intentFilter = IntentFilter(Intent.ACTION_SCREEN_ON).apply {
        addAction(Intent.ACTION_SCREEN_OFF)
        addAction(Intent.ACTION_ANSWER)
        addAction(Intent.ACTION_USER_PRESENT)
    }
    private var registered = false
    private val mediaController = MediaControllerCompat(context, sessionToken)

    private var shouldShowPopup = false

    override fun onReceive(context: Context, intent: Intent) {
        val currentTrack = PlayerQueue.value
        if (currentTrack is LocalSong || currentTrack is AiTrack) return
        if (intent.action == Intent.ACTION_SCREEN_OFF) {
            Log.d(TAG_PLAYER, "onReceive screen off")
            val isPlaying = mediaController.playbackState?.isPlaying == true
            if (isPlaying) {
                Log.d(TAG_PLAYER, "Will pause YTBPlayer due to Youtube policy")
                shouldShowPopup = true
                mediaController.transportControls.pause()
                context.vibrate(longArrayOf(0, 350, 150, 350))
            }
        } else if (intent.action == Intent.ACTION_USER_PRESENT && context.canDrawOverApps()) {
            Log.d(TAG_PLAYER, "onReceive screen on")

            if (SystemSettings.canEnterPiPMode() && shouldShowPopup) {
                shouldShowPopup = false
                context.toast(R.string.battery_saver_mode_instead_of_lock_screen)
            } else if (shouldShowPopup) {
                shouldShowPopup = false
                MaterialDialog(context).show {
                    message(R.string.battery_saver_mode_instead_of_lock_screen) {
                        messageTextView.setTextColor(context.color(R.color.colorWhite))
                        lineSpacing(1.2f)
                    }
                    cancelOnTouchOutside(false)
                    cancelable(false)
                    cornerRadius(literalDp = 8f)
                    positiveButton(R.string.common_turn_on) {
                        mediaController.transportControls.play()
                        val startLockScreenIntent = Intent(context, MainActivity::class.java)
                        startLockScreenIntent.putExtra(
                            MainActivity.EXTRAS_FROM_PLAYER_SERVICE,
                            true
                        )
                        startLockScreenIntent.putExtra(
                            MainActivity.EXTRAS_OPEN_BATTERY_SAVER_MODE,
                            true
                        )
                        startLockScreenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(startLockScreenIntent)
                    }
                    view.setBackgroundColor(context.color(R.color.colorDarkNavigationView))
                    negativeButton(R.string.cancel)
                    getActionButton(WhichButton.NEGATIVE).updateTextColor(Color.parseColor("#808184"))
                    getActionButton(WhichButton.POSITIVE).updateTextColor(Color.parseColor("#D81B60"))
                    window?.setType(windowOverlayTypeOrPhone)
                }
            }
        }
    }

    fun register() {
        if (registered) return
        context.registerReceiver(this, intentFilter)
        registered = true
    }

    fun unregister() {
        if (!registered) return
        context.unregisterReceiver(this)
        registered = false
    }
}