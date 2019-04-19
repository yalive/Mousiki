package com.secureappinc.musicplayer.utils

import android.content.Intent
import androidx.fragment.app.Fragment
import com.secureappinc.musicplayer.MusicApp
import java.io.IOException
import java.nio.charset.Charset

/**
 * Created by Fayssel Yabahddou on 4/13/19.
 */
object Utils {

    fun shareVia(context: Fragment, videoId: String?) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, videoId)
            type = "text/plain"
        }
        context.startActivity(sendIntent)
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
}