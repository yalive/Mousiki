package com.secureappinc.musicplayer.utils

import android.content.Intent
import androidx.fragment.app.Fragment

/**
 * Created by Fayssel Yabahddou on 4/13/19.
 */
class Utils {

    companion object {
        fun shareVia(context: Fragment, videoId: String?) {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, videoId)
                type = "text/plain"
            }
            context.startActivity(sendIntent)
        }
    }
}