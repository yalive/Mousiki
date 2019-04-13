package com.secureappinc.musicplayer.models

import com.secureappinc.musicplayer.MusicApp
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
            return screenHeightPx - height - dpToPixel(28f) // fixed value
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
            return (screenHeightPx - height) / 2 - dpToPixel(80f)
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
            return screenHeightPx - dpToPixel(365.5f) // fixed value
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