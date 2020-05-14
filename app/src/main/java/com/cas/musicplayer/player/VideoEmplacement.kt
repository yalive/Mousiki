package com.cas.musicplayer.player

import android.view.WindowManager
import com.cas.musicplayer.MusicApp
import com.cas.musicplayer.di.ComponentProvider
import com.cas.musicplayer.utils.DeviceInset
import com.cas.musicplayer.utils.UserPrefs
import com.cas.musicplayer.utils.dpToPixel
import com.cas.musicplayer.utils.screenSize

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
        fun bottom(bottomBarVisible: Boolean): EmplacementBottom =
            EmplacementBottom(bottomBarVisible)

        fun center(): EmplacementCenter = EmplacementCenter()

        fun playlist(): EmplacementPlaylist = EmplacementPlaylist()

        fun fullscreen(): EmplacementFullScreen = EmplacementFullScreen()

        fun out(): EmplacementOut = EmplacementOut()
    }
}

class EmplacementBottom(val bottomBarVisible: Boolean) : VideoEmplacement() {

    override val x: Int
        get() {
            return dpToPixel(1f)
        }

    override val y: Int
        get() {
            val connectivityState = (app as ComponentProvider).component.connectivityState
            val notch = if (DeviceInset.hasNotch()) dpToPixel(24f) else 0
            var bottomY =
                screenHeightPx - height - dpToPixel(28f) + DeviceInset.get().top + notch
            if (bottomBarVisible) {
                bottomY -= dpToPixel(56f)
            }
            if (!connectivityState.isConnected()) {
                bottomY -= dpToPixel(20f)
            }
            return bottomY
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
            return dpToPixel(96f)
        }

    override val width: Int
        get() {
            val w = when (UserPrefs.outVideoSize()) {
                UserPrefs.OutVideoSize.SMALL -> 70f
                UserPrefs.OutVideoSize.NORMAL -> 120f
                UserPrefs.OutVideoSize.LARGE -> 200f
            }
            return dpToPixel(w)
        }

    override val height: Int
        get() {
            val h = when (UserPrefs.outVideoSize()) {
                UserPrefs.OutVideoSize.SMALL -> 40f
                UserPrefs.OutVideoSize.NORMAL -> 70f
                UserPrefs.OutVideoSize.LARGE -> 120f
            }
            return dpToPixel(h)
        }
}