package com.secureappinc.musicplayer.models

import android.view.WindowManager
import com.secureappinc.musicplayer.MusicApp
import com.secureappinc.musicplayer.utils.DeviceInset
import com.secureappinc.musicplayer.utils.dpToPixel
import com.secureappinc.musicplayer.utils.screenSize

/**
 **********************************
 * Created by Abdelhadi on 4/11/19.
 **********************************
 */

sealed class VideoEmplacement {
    val app = MusicApp.get()

    val screenWidthPx = app.screenSize().widthPx
    val screenHeightPx = app.screenSize().heightPx

    abstract val x: Int
    abstract val y: Int
    abstract val width: Int
    abstract val height: Int

    fun dpToPixel(dp: Float): Int {
        return app.dpToPixel(dp)
    }

    companion object {
        fun bottom(): EmplacementBottom = EmplacementBottom()
        fun center(): EmplacementCenter = EmplacementCenter()
        fun playlist(): EmplacementPlaylist = EmplacementPlaylist()
        fun fullscreen(): EmplacementFullScreen = EmplacementFullScreen()
        fun out(): EmplacementOut = EmplacementOut()
    }
}

class EmplacementBottom : VideoEmplacement() {

    override val x: Int
        get() {
            return dpToPixel(1f)
        }

    override val y: Int
        get() {

            val notch = if (DeviceInset.hasNotch()) dpToPixel(24f) else 0

            return screenHeightPx - height - dpToPixel(28f) + DeviceInset.get().top + notch
        }

    override val width: Int
        get() {
            return dpToPixel(90f)
        }

    override val height: Int
        get() {
            return dpToPixel(45f)
        }
}

class EmplacementCenter : VideoEmplacement() {
    override val x: Int
        get() {
            return (screenWidthPx - width) / 2
        }

    override val y: Int
        get() {
            return (screenHeightPx - height) / 2 - dpToPixel(40f) + DeviceInset.get().top
        }

    override val width: Int
        get() {
            return dpToPixel(300f)
        }

    override val height: Int
        get() {
            return dpToPixel(150f)
        }
}

class EmplacementPlaylist : VideoEmplacement() {
    override val x: Int
        get() {
            return dpToPixel(7.5f)
        }

    override val y: Int
        get() {
            val notch = if (DeviceInset.hasNotch()) dpToPixel(24f) else 0
            return screenHeightPx - dpToPixel(365.5f) + DeviceInset.get().top + notch
        }

    override val width: Int
        get() {
            return dpToPixel(120f)
        }

    override val height: Int
        get() {
            return dpToPixel(64f)
        }
}

class EmplacementFullScreen : VideoEmplacement() {
    override val x: Int = 0

    override val y: Int = 0

    override val width: Int = WindowManager.LayoutParams.MATCH_PARENT

    override val height: Int = WindowManager.LayoutParams.MATCH_PARENT
}

class EmplacementOut : VideoEmplacement() {
    override val x: Int
        get() {
            return dpToPixel(8f)
        }

    override val y: Int
        get() {
            return dpToPixel(64f)
        }

    override val width: Int
        get() {
            return dpToPixel(120f)
        }

    override val height: Int
        get() {
            return dpToPixel(70f)
        }
}