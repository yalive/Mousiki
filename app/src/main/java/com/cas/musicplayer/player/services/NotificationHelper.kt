package com.cas.musicplayer.player.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Observer
import com.bumptech.glide.request.target.NotificationTarget
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.cas.musicplayer.MusicApp
import com.cas.musicplayer.R
import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.ui.MainActivity
import com.squareup.picasso.Picasso

/**
 **********************************
 * Created by Abdelhadi on 4/20/19.
 **********************************
 */
class NotificationHelper(var service: VideoPlaybackService) {
    val NOTIFY_CHANNEL_ID = "channel_youtube"
    val NOTIFY_CHANNEL_NAME = "channel_youtube_service"
    val NOTIFY_ID = 123

    lateinit var notificationLayout: RemoteViews
    lateinit var notification: Notification
    lateinit var target: NotificationTarget
    lateinit var mRemoteReceiver: BroadcastReceiver

    lateinit var notificationManager: NotificationManager
    lateinit var notifBuilder: NotificationCompat.Builder

    companion object {
        val BUTTON_ID = "BUTTON_ID"
        val ACTION_BUTTON = "ACTION_BUTTON"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("WrongConstant")
    fun init() {
        notificationManager = MusicApp.get().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= 26) {
            notificationManager.createNotificationChannel(
                NotificationChannel(NOTIFY_CHANNEL_ID, NOTIFY_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW)
            )
        }

        this.notificationLayout = RemoteViews(MusicApp.get().packageName, R.layout.notification_playback)

        val intent = Intent(MusicApp.get(), MainActivity::class.java)
        intent.setFlags(335544320)
        notifBuilder = NotificationCompat.Builder(MusicApp.get(), NOTIFY_CHANNEL_ID).setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(
                PendingIntent.getActivity(MusicApp.get(), 2000, intent, 134217728)
            ).setCategory(NotificationCompat.CATEGORY_SERVICE).setPriority(Notification.PRIORITY_LOW)
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setContent(this.notificationLayout)
        this.notification = notifBuilder.build()
        this.target =
            NotificationTarget(MusicApp.get(), R.id.image, this.notificationLayout, this.notification, NOTIFY_ID)

        prepareButtons()

        prepareReceiver()

        registerNotificationReceiver()

        service.startForeground(NOTIFY_ID, notification)

        PlaybackLiveData.observe(service, Observer {
            if (it == PlayerConstants.PlayerState.PAUSED) {
                notificationLayout.setImageViewResource(R.id.action_play_pause, R.drawable.ic_play)
            } else if (it == PlayerConstants.PlayerState.PLAYING) {
                notificationLayout.setImageViewResource(R.id.action_play_pause, R.drawable.ic_pause)
            }
            // update the notification
            notificationManager.notify(NOTIFY_ID, notification);
        })

        PlayerQueue.observe(service, Observer { track ->
            notificationLayout.setTextViewText(R.id.title, track.title)
            Picasso.get().load(track.imgUrl)
                .into(notificationLayout, R.id.image, NOTIFY_ID, notification)
        })

    }

    private fun prepareButtons() {

        addActionToButton(R.id.action_prev, 1)
        addActionToButton(R.id.action_next, 2)
        addActionToButton(R.id.action_play_pause, 3)
        addActionToButton(R.id.action_quit, 4)

    }

    private fun addActionToButton(idRes: Int, buttonIndex: Int) {
        val intent = Intent(ACTION_BUTTON)
        intent.putExtra(BUTTON_ID, idRes)
        var buildPendingIntent = buildPendingIntent(intent, buttonIndex)
        this.notificationLayout.setOnClickPendingIntent(idRes, buildPendingIntent)
    }


    private fun registerNotificationReceiver() {
        MusicApp.get().registerReceiver(this.mRemoteReceiver, IntentFilter(ACTION_BUTTON))
    }


    fun prepareReceiver() {
        this.mRemoteReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val intExtra = intent.getIntExtra(BUTTON_ID, 0)
                if (intExtra == R.id.action_prev) {
                    PlayerQueue.playPreviousTrack()
                } else if (intExtra == R.id.action_next) {
                    PlayerQueue.playNextTrack()
                } else if (intExtra == R.id.action_play_pause) {

                    if (PlaybackLiveData.value == PlayerConstants.PlayerState.PLAYING) {
                        PlayerQueue.pause()
                    } else {
                        PlayerQueue.resume()
                    }

                } else if (intExtra == R.id.action_quit) {
                    service.stopForeground(true)
                    service.stopSelf()
                }

            }
        }
    }

    @SuppressLint("WrongConstant")
    private fun buildPendingIntent(intent: Intent, reqCode: Int): PendingIntent {
        return PendingIntent.getBroadcast(MusicApp.get(), reqCode, intent, 134217728)
    }
}