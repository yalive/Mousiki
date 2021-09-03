package com.cas.musicplayer.ui.local.videos.vplayer

import android.content.ContentResolver
import android.provider.OpenableColumns
import android.media.AudioManager
import com.cas.musicplayer.ui.local.videos.player.VideoPlayerActivity
import android.widget.ImageButton
import com.cas.musicplayer.R
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.widget.FrameLayout
import android.content.pm.PackageManager
import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.media.audiofx.LoudnessEnhancer
import android.net.Uri
import android.os.Build
import android.util.Log
import android.util.Rational
import android.widget.Toast
import android.view.Display
import android.view.View
import androidx.annotation.RequiresApi
import com.arthenica.ffmpegkit.*
import com.cas.musicplayer.BuildConfig
import com.google.android.exoplayer2.Format
import java.io.File
import java.lang.Exception
import java.lang.RuntimeException
import java.util.ArrayList

internal object Utils {
    @JvmStatic
    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    @JvmStatic
    fun pxToDp(px: Float): Float {
        return px / Resources.getSystem().displayMetrics.density
    }

    @JvmStatic
    fun hideSystemUi(playerView: CustomStyledPlayerView) {
        playerView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    @JvmStatic
    fun showSystemUi(playerView: CustomStyledPlayerView) {
        playerView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }

    @JvmStatic
    fun isVolumeMax(audioManager: AudioManager): Boolean {
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == audioManager.getStreamMaxVolume(
            AudioManager.STREAM_MUSIC
        )
    }

    fun isVolumeMin(audioManager: AudioManager): Boolean {
        val min =
            if (Build.VERSION.SDK_INT >= 28) audioManager.getStreamMinVolume(AudioManager.STREAM_MUSIC) else 0
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == min
    }

    @JvmStatic
    fun adjustVolume(
        audioManager: AudioManager,
        playerView: CustomStyledPlayerView,
        raise: Boolean,
        canBoost: Boolean,
        loudnessEnhancer: LoudnessEnhancer,
        boostLevel: Int
    ):Int {
        val volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val volumeMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        var volumeActive = volume != 0

        var aa = boostLevel
        if (volume != volumeMax) {
            aa = 0
        }

        if (volume != volumeMax || aa == 0 && !raise) {
            loudnessEnhancer.enabled = false
            audioManager.adjustStreamVolume(
                AudioManager.STREAM_MUSIC,
                if (raise) AudioManager.ADJUST_RAISE else AudioManager.ADJUST_LOWER,
                AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE
            )
            val volumeNew = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            if (raise && volume == volumeNew && !isVolumeMin(audioManager)) {
                audioManager.adjustStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    AudioManager.ADJUST_RAISE,
                    AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE or AudioManager.FLAG_SHOW_UI
                )
            } else {
                volumeActive = volumeNew != 0
                playerView.setCustomErrorMessage(if (volumeActive) " $volumeNew" else "")
            }
        } else {
            if (canBoost && raise && aa < 10) aa++ else if (!raise && aa > 0) aa--
            try {
                loudnessEnhancer.setTargetGain(aa * 200)
            } catch (e: RuntimeException) {
                e.printStackTrace()
            }
            playerView.setCustomErrorMessage(" " + (volumeMax + aa))
        }
        playerView.setIconVolume(volumeActive)
        loudnessEnhancer.enabled = aa > 0
        playerView.setHighlight(aa > 0)

        return aa
    }

    @JvmStatic
    fun setButtonEnabled(context: Context, button: ImageButton, enabled: Boolean) {
        button.isEnabled = enabled
        button.alpha =
            if (enabled) context.resources.getInteger(R.integer.exo_media_button_opacity_percentage_enabled)
                .toFloat() / 100 else context.resources.getInteger(R.integer.exo_media_button_opacity_percentage_disabled)
                .toFloat() / 100
    }

    @JvmStatic
    @JvmOverloads
    fun showText(playerView: CustomStyledPlayerView, text: String?, timeout: Long = 1200) {
        playerView.removeCallbacks(playerView.textClearRunnable)
        playerView.clearIcon()
        playerView.setCustomErrorMessage(text)
        playerView.postDelayed(playerView.textClearRunnable, timeout)
    }

    @JvmStatic
    @SuppressLint("SourceLockedOrientationActivity")
    fun setOrientation(activity: Activity, orientation: Orientation?, format: Format) {
        when (orientation) {
            Orientation.VIDEO -> {
                if (isPortrait(format))
                    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
                else
                    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            }
            Orientation.SENSOR -> activity.requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_SENSOR
        }
    }

    @JvmStatic
    fun getNextOrientation(orientation: Orientation?): Orientation {
        return when (orientation) {
            Orientation.VIDEO -> Orientation.SENSOR
            Orientation.SENSOR -> Orientation.VIDEO
            else -> Orientation.VIDEO
        }
    }

    fun isRotated(format: Format): Boolean {
        return format.rotationDegrees == 90 || format.rotationDegrees == 270
    }

    @JvmStatic
    fun isPortrait(format: Format): Boolean {
        return if (isRotated(format)) {
            format.width > format.height
        } else {
            format.height > format.width
        }
    }

    @JvmStatic
    fun getRational(format: Format): Rational {
        return if (isRotated(format)) Rational(
            format.height,
            format.width
        ) else Rational(format.width, format.height)
    }

    @JvmStatic
    fun formatMilis(time: Long): String {
        val totalSeconds = Math.abs(time.toInt() / 1000)
        val seconds = totalSeconds % 60
        val minutes = totalSeconds % 3600 / 60
        val hours = totalSeconds / 3600
        return if (hours > 0) String.format(
            "%d:%02d:%02d",
            hours,
            minutes,
            seconds
        ) else String.format("%02d:%02d", minutes, seconds)
    }

    @JvmStatic
    fun formatMilisSign(time: Long): String {
        return if (time > -1000 && time < 1000) formatMilis(
            time
        ) else (if (time < 0) "−" else "+") + formatMilis(
            time
        )
    }

    fun log(text: String?) {
        if (BuildConfig.DEBUG) {
            Log.d("JustPlayer", text!!)
        }
    }

    @JvmStatic
    fun setViewMargins(
        view: View,
        marginLeft: Int,
        marginTop: Int,
        marginRight: Int,
        marginBottom: Int
    ) {
        val layoutParams = view.layoutParams as FrameLayout.LayoutParams
        layoutParams.setMargins(marginLeft, marginTop, marginRight, marginBottom)
        view.layoutParams = layoutParams
    }

    @JvmStatic
    fun setViewParams(
        view: View,
        paddingLeft: Int,
        paddingTop: Int,
        paddingRight: Int,
        paddingBottom: Int,
        marginLeft: Int,
        marginTop: Int,
        marginRight: Int,
        marginBottom: Int
    ) {
        view.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
        setViewMargins(view, marginLeft, marginTop, marginRight, marginBottom)
    }

    @JvmStatic
    fun isTvBox(context: Context): Boolean {
        // TV for sure
        val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        if (uiModeManager.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION) {
            return true
        }

        // Android box (non Android TV) or phone connected to external display (desktop mode)
        if (Build.VERSION.SDK_INT < 29) {
            // Most likely not a box
            if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN)) {
                return false
            }

            // Missing Files app (DocumentsUI) means box
            // Some boxes have non functional app or stub (!)
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "video/*"
            if (intent.resolveActivity(context.packageManager) == null) {
                return true
            }
        }

        // Default: No TV - use SAF
        return false
    }

    fun normRate(rate: Float): Int {
        return (rate * 100f).toInt()
    }

    @JvmStatic
    fun switchFrameRate(
        activity: VideoPlayerActivity,
        frameRateExo: Float,
        uri: Uri,
        play: Boolean
    ): Boolean {
        if (!isTvBox(activity)) return false

        // preferredDisplayModeId only available on SDK 23+
        // ExoPlayer already uses Surface.setFrameRate() on Android 11+ but may not detect actual video frame rate
        return if (Build.VERSION.SDK_INT >= 23 && (Build.VERSION.SDK_INT < 30 || frameRateExo == Format.NO_VALUE.toFloat())) {
            val path: String
            path = if (ContentResolver.SCHEME_CONTENT == uri.scheme) {
                FFmpegKitConfig.getSafParameterForRead(activity, uri)
            } else if (ContentResolver.SCHEME_FILE == uri.scheme) {
                // TODO: FFprobeKit doesn't accept encoded uri (like %20) (?!)
                uri.schemeSpecificPart
            } else {
                uri.toString()
            }
            // Use ffprobe as ExoPlayer doesn't detect video frame rate for lots of videos
            // and has different precision than ffprobe (so do not mix that)
            FFprobeKit.getMediaInformationAsync(path, { session: Session? ->
                if (session == null) return@getMediaInformationAsync
                var frameRate = Format.NO_VALUE.toFloat()
                val mediaInformationSession: MediaInformationSession
                mediaInformationSession =
                    if (session is MediaInformationSession) session else return@getMediaInformationAsync
                val mediaInformation = mediaInformationSession.mediaInformation
                    ?: return@getMediaInformationAsync
                val streamInformations = mediaInformation.streams
                for (streamInformation in streamInformations) {
                    if (streamInformation.type == "video") {
                        val averageFrameRate = streamInformation.averageFrameRate
                        if (averageFrameRate.contains("/")) {
                            val vals = averageFrameRate.split("/").toTypedArray()
                            frameRate = vals[0].toFloat() / vals[1].toFloat()
                            break
                        }
                    }
                }
                handleFrameRate(activity, frameRate, play)
            }, null, 8000)
            true
        } else {
            false
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun handleFrameRate(activity: VideoPlayerActivity, frameRate: Float, play: Boolean) {
        activity.runOnUiThread {
            if (BuildConfig.DEBUG) Toast.makeText(
                activity,
                "Video frameRate: $frameRate",
                Toast.LENGTH_LONG
            ).show()
            if (frameRate > 0) {
                val display = activity.window.decorView.display
                val supportedModes = display.supportedModes
                val activeMode = display.mode
                if (supportedModes.size > 1) {
                    // Refresh rate >= video FPS
                    val modesHigh: MutableList<Display.Mode> = ArrayList()
                    // Max refresh rate
                    var modeTop = activeMode
                    var modesResolutionCount = 0

                    // Filter only resolutions same as current
                    for (mode in supportedModes) {
                        if (mode.physicalWidth == activeMode.physicalWidth &&
                            mode.physicalHeight == activeMode.physicalHeight
                        ) {
                            modesResolutionCount++
                            if (normRate(mode.refreshRate) >= normRate(frameRate)) modesHigh.add(
                                mode
                            )
                            if (normRate(mode.refreshRate) > normRate(modeTop.refreshRate)) modeTop =
                                mode
                        }
                    }
                    if (modesResolutionCount > 1) {
                        var modeBest: Display.Mode? = null
                        for (mode in modesHigh) {
                            if (normRate(mode.refreshRate) % normRate(frameRate) <= 0.0001f) {
                                if (modeBest == null || normRate(mode.refreshRate) > normRate(
                                        modeBest.refreshRate
                                    )
                                ) {
                                    modeBest = mode
                                }
                            }
                        }
                        val window = activity.window
                        val layoutParams = window.attributes
                        if (modeBest == null) modeBest = modeTop
                        val switchingModes = modeBest!!.modeId != activeMode.modeId
                        if (switchingModes) {
                            layoutParams.preferredDisplayModeId = modeBest.modeId
                            window.attributes = layoutParams
                        } else {
                            if (play) {
                                if (activity.player != null) activity.player.play()
                                if (activity.playerView != null) activity.playerView.hideController()
                            }
                        }
                        if (BuildConfig.DEBUG) Toast.makeText(
                            activity, """
     Video frameRate: $frameRate
     Display refreshRate: ${modeBest.getRefreshRate()}
     """.trimIndent(), Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    enum class Orientation(val value: Int, val description: Int) {
        VIDEO(0, R.string.video_orientation_video), SENSOR(1, R.string.video_orientation_sensor);
    }
}