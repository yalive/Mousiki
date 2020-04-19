package com.cas.musicplayer.utils

import android.app.Activity
import android.app.KeyguardManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.audiofx.AudioEffect
import android.net.Uri
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.NonNull
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.callbacks.onShow
import com.afollestad.materialdialogs.customview.customView
import com.cas.musicplayer.MusicApp
import com.cas.musicplayer.R
import com.cas.musicplayer.domain.model.MusicTrack
import com.google.firebase.dynamiclinks.ShortDynamicLink
import com.google.firebase.dynamiclinks.ktx.*
import com.google.firebase.ktx.Firebase
import java.io.File
import java.util.*


/**
 * Created by Fayssel Yabahddou on 4/13/19.
 */
object Utils {

    var hasShownAdsOneTime = false

    fun shareVia(videoId: String?, mContext: Context) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, videoId)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            type = "text/plain"
        }

        val context = MusicApp.get()
        if (sendIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(sendIntent)
        }
    }


    fun shareWithDeepLink(track: MusicTrack?, mContext: Context) {
        Firebase.dynamicLinks.shortLinkAsync(ShortDynamicLink.Suffix.SHORT) {
            link = Uri.Builder()
                .scheme("https")
                .authority("www.mouziki.com")
                .appendQueryParameter("videoId", track?.youtubeId)
                .appendQueryParameter("title", track?.title)
                .appendQueryParameter("duration", track?.duration)
                .build()
            domainUriPrefix = "https://mouziki.page.link"
            androidParameters { }
            iosParameters("com.mouziki.ios") { }
            socialMetaTagParameters {
                title = track?.title ?: ""
                description = ""
                imageUrl = Uri.parse(track?.imgUrl)
            }
        }.addOnSuccessListener { result ->
            val shortLink = result.shortLink
            Log.d("dynamicLinks", "Dynamic link:${shortLink.toString()}")
            shareVia(shortLink.toString(), mContext)
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
        }
    }

    fun sendEmail(context: Context) {
        val emailIntent = Intent(
            Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "memory.games.apps@gmail.com", null
            )
        )
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name))
        emailIntent.putExtra(Intent.EXTRA_TEXT, "")
        context.startActivity(Intent.createChooser(emailIntent, "Send email..."))
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
        }
    }

    fun fileContent(file: File) = file.inputStream().bufferedReader().use { it.readText() }

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
}


fun isScreenLocked(): Boolean {
    val myKM = MusicApp.get().getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
    return myKM.isKeyguardLocked
}

fun getCurrentLocale(): String {
    val tm = MusicApp.get().getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
    val countryCodeValue: String? = tm?.networkCountryIso

    if (countryCodeValue != null) {
        return countryCodeValue
    }

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        MusicApp.get().resources.configuration.locales.get(0).country
    } else {

        MusicApp.get().resources.configuration.locale.country
    }
}

fun getLanguage(): String {
    val language = Locale.getDefault().language.toLowerCase()
    if (language.isEmpty()) {
        return "en"
    }
    return language
}