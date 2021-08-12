package com.cas.musicplayer.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.KeyguardManager
import android.content.ActivityNotFoundException
import android.content.ContentUris.withAppendedId
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaMetadataRetriever
import android.media.audiofx.AudioEffect
import android.net.Uri
import android.provider.Settings
import android.text.TextUtils
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.callbacks.onShow
import com.afollestad.materialdialogs.customview.customView
import com.cas.musicplayer.MusicApp
import com.cas.musicplayer.R
import com.cas.musicplayer.ui.local.artists.model.LocalArtist
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.dynamiclinks.ShortDynamicLink
import com.google.firebase.dynamiclinks.ktx.*
import com.google.firebase.ktx.Firebase
import com.mousiki.shared.domain.models.*
import com.mousiki.shared.utils.AnalyticsApi
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import java.io.File
import java.util.*


/**
 * Created by Fayssel Yabahddou on 4/13/19.
 */
object Utils : KoinComponent {

    var hasShownAdsOneTime = false

    fun shareTrack(track: Track?, mContext: Context) {
        if (track == null) return
        if (track is LocalSong) {
            val intent = createShareSongFileIntent(track.song, mContext)
            if (intent?.resolveActivity(mContext.packageManager) != null) {
                mContext.startActivity(intent)
                get<AnalyticsApi>().logEvent(ANALYTICS_CREATE_TRACK_DYNAMIC_LINK)
            }
        } else if (track is YtbTrack) {
            Firebase.dynamicLinks.shortLinkAsync(ShortDynamicLink.Suffix.SHORT) {
                link = Uri.Builder()
                    .scheme("https")
                    .authority("www.mouziki.com")
                    .appendQueryParameter("videoId", track.youtubeId)
                    .appendQueryParameter("title", track.title)
                    .appendQueryParameter("duration", track.duration)
                    .build()
                domainUriPrefix = "https://mouziki.page.link"
                androidParameters {
                }
                iosParameters("com.mouziki.ios") { }
                socialMetaTagParameters {
                    title = track.title
                    description = ""
                    imageUrl = Uri.parse(track.imgUrl)

                }
            }.addOnSuccessListener { result ->
                val shortLink = result.shortLink
                shareYtbTrackLink(shortLink.toString(), track, mContext)
            }
        }
    }

    private fun shareYtbTrackLink(link: String?, track: YtbTrack, context: Context) {
        val text = context.getString(R.string.share_track_link_message, track?.title, link)
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            type = "text/plain"
        }
        if (sendIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(sendIntent)
            get<AnalyticsApi>().logEvent(ANALYTICS_CREATE_TRACK_DYNAMIC_LINK)
        }
    }

    private fun createShareSongFileIntent(song: Song, context: Context): Intent? {
        return try {
            Intent().setAction(Intent.ACTION_SEND).putExtra(
                Intent.EXTRA_STREAM,
                FileProvider.getUriForFile(
                    context,
                    context.applicationContext.packageName,
                    File(song.data)
                )
            ).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION).setType("audio/*")
        } catch (e: IllegalArgumentException) {
            // TODO the path is most likely not like /storage/emulated/0/... but something like /storage/28C7-75B0/...
            e.printStackTrace()
            Toast.makeText(
                context,
                "Could not share this file, I'm aware of the issue.",
                Toast.LENGTH_SHORT
            ).show()
            Intent()
        }
    }


    fun shareAppVia() {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                "Install Free Music App from: https://play.google.com/store/apps/details?id=com.cas.musicplayer"
            )
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            type = "text/plain"
        }
        val context = MusicApp.get()
        if (sendIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(sendIntent)
            get<AnalyticsApi>().logEvent(ANALYTICS_SHARE_APP_VIA)
        }
    }

    fun sendEmail(context: Context, text: String) {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf("contact.mousiki@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name))
            putExtra(Intent.EXTRA_TEXT, text)
        }
        if (emailIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(Intent.createChooser(emailIntent, "Send email..."))
        } else {
            // No app found
        }
    }

    fun openEqualizer(@NonNull activity: Activity) {
        /*MusicPlayerRemote.getAudioSessionId()*/
        val audioSessionId = 0
        if (audioSessionId == AudioEffect.ERROR_BAD_VALUE) {
            activity.toast(activity.resources.getString(com.cas.musicplayer.R.string.no_audio_ID))
        } else {
            try {
                val intent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
                intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, audioSessionId)
                intent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
                activity.startActivityForResult(intent, 0)
                return
            } catch (unused: ActivityNotFoundException) {
                activity.toast(activity.resources.getString(com.cas.musicplayer.R.string.no_equalizer))
            }
        }
    }

    fun openWebview(context: Context, url: String) {
        val webView = WebView(context.applicationContext)
        val dialog = MaterialDialog(context).show {
            customView(null, webView)
            negativeButton(text = context.getString(R.string.btn_close))
            cancelOnTouchOutside(false)
        }
        dialog.onShow {
            val settings = webView.settings
            settings.javaScriptEnabled = true
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true
            settings.setSupportZoom(true)
            settings.cacheMode = WebSettings.LOAD_NO_CACHE
            webView.webViewClient = WebViewClient()
            webView.loadUrl(url)
        }
        dialog.onDismiss {
            webView.destroy()
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
    }

    fun openFacebookPage(context: Context) {
        val url = "https://www.facebook.com/mousiki2"
        var uri = Uri.parse(url)
        try {
            val applicationInfo =
                context.packageManager.getApplicationInfo("com.facebook.katana", 0);
            if (applicationInfo.enabled) {
                uri = Uri.parse("fb://facewebmodal/f?href=" + url);
            }
        } catch (ignored: PackageManager.NameNotFoundException) {
        }
        val intent = Intent(Intent.ACTION_VIEW, uri)
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
            get<AnalyticsApi>().logEvent(ANALYTICS_OPEN_FACEBOOK_PAGE)
        }
    }

    fun fileContent(file: File): String = try {
        file.inputStream().bufferedReader().use { it.readText() }
    } catch (e: Exception) {
        FirebaseCrashlytics.getInstance().recordException(e)
        ""
    } catch (error: OutOfMemoryError) {
        FirebaseCrashlytics.getInstance().recordException(error)
        ""
    }

    fun writeToFile(content: String, file: File) =
        file.outputStream().bufferedWriter().use { it.write(content) }

    fun openInPlayStore(context: Context) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(
                "https://play.google.com/store/apps/details?id=com.cas.musicplayer"
            )
            setPackage("com.android.vending")
        }
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }

    @SuppressLint("InlinedApi")
    inline fun requestDrawOverAppsPermission(
        context: Context,
        crossinline onRequested: () -> Unit
    ): AlertDialog {
        return AlertDialog.Builder(context).setCancelable(false)
            .setMessage(R.string.message_enable_draw)
            .setNegativeButton(context.getString(R.string.btn_deny)) { _, _ ->
            }.setPositiveButton(context.getString(R.string.btn_agree)) { _, _ ->
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:${context.packageName}")
                )
                if (intent.resolveActivity(context.packageManager) != null) {
                    onRequested()
                    if (context is AppCompatActivity) {
                        context.startActivityForResult(intent, 10)
                    } else {
                        context.startActivity(intent)
                    }
                } else {
                    MusicApp.get().toast(R.string.message_enable_draw_over_apps_manually)
                    FirebaseCrashlytics.getInstance()
                        .log("requestDrawOverAppsPermission intent not resolved")
                }
            }.show()
    }

    fun getAlbumArtUri(albumId: Long) =
        withAppendedId("content://media/external/audio/albumart".toUri(), albumId)

    fun isVariousArtists(artistName: String?): Boolean {
        if (TextUtils.isEmpty(artistName)) {
            return false
        }
        if (artistName == LocalArtist.VARIOUS_ARTISTS_DISPLAY_NAME) {
            return true
        }
        return false
    }

    fun isArtistNameUnknown(artistName: String?): Boolean {
        if (TextUtils.isEmpty(artistName)) {
            return false
        }
        if (artistName == LocalArtist.UNKNOWN_ARTIST_DISPLAY_NAME) {
            return true
        }
        val tempName = artistName!!.trim { it <= ' ' }.toLowerCase(Locale.getDefault())
        return tempName == "unknown" || tempName == "<unknown>"
    }

    fun getStoragePaths(context: Context): List<String> {
        return try {
            val paths = ContextCompat.getExternalFilesDirs(context, null).filterNotNull()
            paths.map {
                it.path.replace("/Android/data/${context.packageName}/files", "")
            }
        } catch (ex: Exception) {
            emptyList()
        }
    }

    fun getSongThumbnail(songPath: String): ByteArray? {
        var imgByte: ByteArray?
        MediaMetadataRetriever().also {
            try {
                it.setDataSource(songPath)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            imgByte = it.embeddedPicture
            it.release()
        }
        return imgByte
    }
}


fun isScreenLocked(): Boolean {
    val myKM = MusicApp.get().getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
    return myKM.isKeyguardLocked
}

private const val ANALYTICS_CREATE_TRACK_DYNAMIC_LINK = "create_track_dynamic_link"
private const val ANALYTICS_OPEN_FACEBOOK_PAGE = "open_facebook_page"
private const val ANALYTICS_SHARE_APP_VIA = "share_app_with_via"