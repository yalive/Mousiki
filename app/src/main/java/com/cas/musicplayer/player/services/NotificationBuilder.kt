package com.cas.musicplayer.player.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.media.session.MediaButtonReceiver
import com.cas.musicplayer.R
import com.cas.musicplayer.player.extensions.isPlaying
import com.cas.musicplayer.ui.MainActivity
import com.cas.musicplayer.utils.UserPrefs
import com.cas.musicplayer.utils.getBitmap
import com.squareup.picasso.Picasso

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/21/20.
 ***************************************
 */

const val NOW_PLAYING_CHANNEL: String = "com.mousiki.media.NOW_PLAYING"
const val NOW_PLAYING_NOTIFICATION: Int = 0xb339

class NotificationBuilder(private val context: Context) {

    private val platformNotificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val skipToPreviousAction = NotificationCompat.Action(
        R.drawable.ic_skip_previous,
        context.getString(R.string.player_notification_skip_to_previous),
        MediaButtonReceiver.buildMediaButtonPendingIntent(
            context,
            PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
        )
    )

    private val playAction = NotificationCompat.Action(
        R.drawable.ic_play,
        context.getString(R.string.player_notification_play),
        MediaButtonReceiver.buildMediaButtonPendingIntent(
            context,
            PlaybackStateCompat.ACTION_PLAY
        )
    )

    private val pauseAction = NotificationCompat.Action(
        R.drawable.ic_pause,
        context.getString(R.string.player_notification_pause),
        MediaButtonReceiver.buildMediaButtonPendingIntent(
            context,
            PlaybackStateCompat.ACTION_PAUSE
        )
    )

    private val skipToNextAction = NotificationCompat.Action(
        R.drawable.ic_skip_next,
        context.getString(R.string.player_notification_skip_to_next),
        MediaButtonReceiver.buildMediaButtonPendingIntent(
            context,
            PlaybackStateCompat.ACTION_SKIP_TO_NEXT
        )
    )

    private val stopPendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        Intent(DeleteNotificationReceiver.ACTION_DELETE_NOTIFICATION),
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    private val stopIntentReceiver =
        Intent(context, DeleteNotificationReceiver::class.java)
    private val stopPendingIntent2 = PendingIntent.getBroadcast(
        context,
        0,
        stopIntentReceiver,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    suspend fun buildNotification(sessionToken: MediaSessionCompat.Token): Notification {
        if (shouldCreateNowPlayingChannel()) {
            createNowPlayingChannel()
        }

        val controller = MediaControllerCompat(context, sessionToken)
        val description = controller.metadata?.description
        val playbackState = controller.playbackState
        val builder = NotificationCompat.Builder(context, NOW_PLAYING_CHANNEL)

        if (UserPrefs.isFav(description?.mediaId)) {
            builder.addAction(
                R.drawable.ic_heart_solid,
                context.getString(R.string.player_remove_from_favourite),
                createFavouriteIntent(false)
            )
        } else {
            builder.addAction(
                R.drawable.ic_heart_light,
                context.getString(R.string.player_add_to_favourite),
                createFavouriteIntent(true)
            )
        }

        // Only add actions for skip back, play/pause, skip forward, based on what's enabled.
        builder.addAction(skipToPreviousAction)
        if (playbackState.isPlaying) {
            builder.addAction(pauseAction)
        } else {
            builder.addAction(playAction)
        }
        builder.addAction(skipToNextAction)

        val mediaStyle = androidx.media.app.NotificationCompat.MediaStyle()
            .setMediaSession(sessionToken)
            .setShowActionsInCompactView(1, 2, 3)

        val largeIconBitmap = description?.mediaUri?.toString()?.let {
            Picasso.get().getBitmap(it)
        }
        return builder.setContentIntent(controller.sessionActivity)
            .setContentText(description?.subtitle)
            .setContentTitle(description?.title)
            .setContentIntent(contentIntent())
            .setDeleteIntent(stopPendingIntent)
            .setLargeIcon(largeIconBitmap)
            .setOnlyAlertOnce(true)
            .setSmallIcon(R.drawable.ic_app_player_notification)
            .apply {
                if (!isLollipopHuawei()) {
                    setStyle(mediaStyle)
                }
            }
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    private fun createFavouriteIntent(addToFav: Boolean): PendingIntent {
        val intentFav = Intent(FavouriteReceiver.ACTION_FAVOURITE)
        intentFav.putExtra(FavouriteReceiver.EXTRAS_ADD_TO_FAVOURITE, addToFav)
        return PendingIntent.getBroadcast(
            context, 0, intentFav, PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun shouldCreateNowPlayingChannel() =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !nowPlayingChannelExists()

    @RequiresApi(Build.VERSION_CODES.O)
    private fun nowPlayingChannelExists() =
        platformNotificationManager.getNotificationChannel(NOW_PLAYING_CHANNEL) != null

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNowPlayingChannel() {
        val notificationChannel = NotificationChannel(
            NOW_PLAYING_CHANNEL,
            context.getString(R.string.notification_channel),
            NotificationManager.IMPORTANCE_LOW
        )
            .apply {
                description = context.getString(R.string.notification_channel_description)
            }

        platformNotificationManager.createNotificationChannel(notificationChannel)
    }

    private fun contentIntent(): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra(MainActivity.EXTRAS_FROM_PLAY_SERVICE, true)
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun isLollipopHuawei(): Boolean {
        return (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP_MR1
                || Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP)
                && "HUAWEI".equals(Build.MANUFACTURER, true)
    }
}