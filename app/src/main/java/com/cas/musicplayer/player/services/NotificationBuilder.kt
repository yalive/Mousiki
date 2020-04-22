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
import com.cas.musicplayer.player.extensions.isPlayEnabled
import com.cas.musicplayer.player.extensions.isPlaying
import com.cas.musicplayer.player.extensions.isSkipToNextEnabled
import com.cas.musicplayer.player.extensions.isSkipToPreviousEnabled
import com.cas.musicplayer.utils.UserPrefs
import com.cas.musicplayer.utils.loadBitmap
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
    private val stopPendingIntent =
        MediaButtonReceiver.buildMediaButtonPendingIntent(
            context,
            PlaybackStateCompat.ACTION_STOP
        )

    suspend fun buildNotification(sessionToken: MediaSessionCompat.Token): Notification {
        if (shouldCreateNowPlayingChannel()) {
            createNowPlayingChannel()
        }

        val controller = MediaControllerCompat(context, sessionToken)
        val description = controller.metadata.description
        val playbackState = controller.playbackState

        val builder = NotificationCompat.Builder(context, NOW_PLAYING_CHANNEL)

        if (UserPrefs.isFav(description.mediaId)) {
            builder.addAction(
                R.drawable.ic_favorite_added_24dp,
                context.getString(R.string.player_remove_from_favourite),
                createFavouriteIntent(false)
            )
        } else {
            builder.addAction(
                R.drawable.ic_favorite_border,
                context.getString(R.string.player_add_to_favourite),
                createFavouriteIntent(true)
            )
        }

        // Only add actions for skip back, play/pause, skip forward, based on what's enabled.
        var playPauseIndex = 0
        if (playbackState.isSkipToPreviousEnabled) {
            builder.addAction(skipToPreviousAction)
            ++playPauseIndex
        }
        if (playbackState.isPlaying) {
            builder.addAction(pauseAction)
        } else if (playbackState.isPlayEnabled) {
            builder.addAction(playAction)
        }
        if (playbackState.isSkipToNextEnabled) {
            builder.addAction(skipToNextAction)
        }
        val mediaStyle = androidx.media.app.NotificationCompat.MediaStyle()
            .setCancelButtonIntent(stopPendingIntent)
            .setMediaSession(sessionToken)
            .setShowActionsInCompactView(0, 1, 2)
            .setShowCancelButton(true)

        val largeIconBitmap = description.mediaUri?.toString()?.let {
            Picasso.get().loadBitmap(it)
        }

        return builder.setContentIntent(controller.sessionActivity)
            .setContentText(description.subtitle)
            .setContentTitle(description.title)
            .setDeleteIntent(stopPendingIntent)
            .setLargeIcon(largeIconBitmap)
            .setOnlyAlertOnce(true)
            .setOngoing(playbackState.state == PlaybackStateCompat.STATE_PLAYING)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setStyle(mediaStyle)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    private fun createFavouriteIntent(addToFav: Boolean): PendingIntent {
        val intentFav = Intent(FavouriteReceiver.ACTION_FAVOURITE)
        intentFav.putExtra(FavouriteReceiver.EXTRAS_ADD_TO_FAVOURITE, addToFav)
        return PendingIntent.getBroadcast(
            context, 19, intentFav, PendingIntent.FLAG_UPDATE_CURRENT
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
}