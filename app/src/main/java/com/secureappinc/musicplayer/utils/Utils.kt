package com.secureappinc.musicplayer.utils

import android.app.Activity
import android.app.KeyguardManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.audiofx.AudioEffect
import android.net.Uri
import android.os.Build
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import androidx.annotation.NonNull
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.callbacks.onShow
import com.afollestad.materialdialogs.customview.customView
import com.secureappinc.musicplayer.MusicApp
import java.io.IOException
import java.nio.charset.Charset
import java.util.*


/**
 * Created by Fayssel Yabahddou on 4/13/19.
 */
object Utils {

    var hasShownAdsOneTime = false

    fun shareVia(videoId: String?) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, videoId)
            type = "text/plain"
        }

        // TODO: Resolve intent first
        MusicApp.get().startActivity(sendIntent)
    }

    fun shareAppVia() {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                "Install Free Music App from: https://play.google.com/store/apps/details?id=com.secureappinc.freemusic"
            )
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            type = "text/plain"
        }

        // TODO: Resolve intent first
        MusicApp.get().startActivity(sendIntent)
    }

    fun shareFeedback() {
        val emailIntent = Intent(
            Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "secureappinc@gmail.com", null
            )
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Free Music App")
        emailIntent.putExtra(Intent.EXTRA_TEXT, "")
        MusicApp.get().startActivity(Intent.createChooser(emailIntent, "Send email..."))
    }

    /**
     * Load json from asset file
     *
     * [assetFileName] File name
     * @return Json as String
     */
    fun loadStringJSONFromAsset(assetFileName: String): String {
        try {
            val `is` = MusicApp.get().assets.open(assetFileName)
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            return String(buffer, Charset.forName("UTF-8"))

        } catch (ignored: IOException) {
        }
        return "{}"
    }

    fun openEqualizer(@NonNull activity: Activity) {
        /*MusicPlayerRemote.getAudioSessionId()*/
        val audioSessionId = 0
        if (audioSessionId == AudioEffect.ERROR_BAD_VALUE) {
            activity.toast(activity.resources.getString(com.secureappinc.musicplayer.R.string.no_audio_ID))
        } else {
            try {
                val intent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
                intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, audioSessionId)
                intent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
                activity.startActivityForResult(intent, 0)
                return
            } catch (unused: ActivityNotFoundException) {
                activity.toast(activity.resources.getString(com.secureappinc.musicplayer.R.string.no_equalizer))
            }
        }
    }


    fun openWebview(context: Context, url: String) {

        val webView = WebView(context)
        val dialog = MaterialDialog(context).show {
            customView(null, webView)
            negativeButton(text = "Close")
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

    fun rateApp(context: Context) {
        val view = LayoutInflater.from(context).inflate(com.secureappinc.musicplayer.R.layout.dialog_rate, null)
        val btnRate = view.findViewById<Button>(com.secureappinc.musicplayer.R.id.btnRate)
        val btnRemindMe = view.findViewById<Button>(com.secureappinc.musicplayer.R.id.btnRemindMe)
        val btnNoThanks = view.findViewById<Button>(com.secureappinc.musicplayer.R.id.btnNoThanks)
        val dialog = MaterialDialog(context).show {
            customView(null, view, false, true)
            cancelOnTouchOutside(false)
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))

        btnRate.setOnClickListener {
            dialog.dismiss()
            UserPrefs.setRatedApp()
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(
                    "https://play.google.com/store/apps/details?id=com.secureappinc.freemusic"
                )
                setPackage("com.android.vending")
            }
            context.startActivity(intent)
        }

        btnRemindMe.setOnClickListener {
            dialog.dismiss()
        }

        btnNoThanks.setOnClickListener {
            dialog.dismiss()
        }
    }
}

fun isScreenLocked(): Boolean {
    val myKM = MusicApp.get().getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
    return myKM.isKeyguardLocked
}

fun getCurrentLocale(): String {
    val tm = MusicApp.get().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    val countryCodeValue = tm.networkCountryIso

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