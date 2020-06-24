package com.cas.musicplayer.player

import android.view.WindowManager
import com.cas.musicplayer.MusicApp
import com.cas.musicplayer.utils.UserPrefs
import com.cas.musicplayer.utils.dpToPixel

/**
 **********************************
 * Created by Abdelhadi on 4/11/19.
 **********************************
 */

sealed class VideoEmplacement {
    val app = MusicApp.get()
    abstract val x: Int
    abstract val y: Int
    abstract val width: Int
    abstract val height: Int
    abstract val radius: Float

    fun dpToPixel(dp: Float): Int {
        return app.dpToPixel(dp)
    }

    companion object {

        fun fullscreen(): EmplacementFullScreen = EmplacementFullScreen()

        fun out(): EmplacementOut = EmplacementOut()
    }
}

class EmplacementInApp : VideoEmplacement() {
    override val width: Int = 0
    override val height: Int = 0
    override val x: Int = 0
    override val y: Int = 0
    override val radius: Float = 0f
}

class EmplacementFullScreen : VideoEmplacement() {
    override val x: Int = 0

    override val y: Int = 0

    override val width: Int = WindowManager.LayoutParams.MATCH_PARENT

    override val height: Int = WindowManager.LayoutParams.MATCH_PARENT

    override val radius: Float
        get() = dpToPixel(8f).toFloat()
}

class EmplacementOut : VideoEmplacement() {
    override val x: Int
        get() {
            return dpToPixel(8f)
        }

    override val y: Int
        get() {
            return dpToPixel(96f)
        }

    override val width: Int
        get() {
            val w = when (UserPrefs.outVideoSize()) {
                UserPrefs.OutVideoSize.SMALL -> 70f
                UserPrefs.OutVideoSize.NORMAL -> 120f
                UserPrefs.OutVideoSize.LARGE -> 200f
                UserPrefs.OutVideoSize.CIRCULAR -> 60f
            }
            return dpToPixel(w)
        }

    override val height: Int
        get() {
            val h = when (UserPrefs.outVideoSize()) {
                UserPrefs.OutVideoSize.SMALL -> 40f
                UserPrefs.OutVideoSize.NORMAL -> 70f
                UserPrefs.OutVideoSize.LARGE -> 120f
                UserPrefs.OutVideoSize.CIRCULAR -> 60f
            }
            return dpToPixel(h)
        }

    override val radius: Float
        get() {
            val radiusDp = when (UserPrefs.outVideoSize()) {
                UserPrefs.OutVideoSize.CIRCULAR -> 30f
                else -> 4f
            }
            return dpToPixel(radiusDp).toFloat()
        }
}