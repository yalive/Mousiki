package com.cas.musicplayer.ui.settings.rate

import android.content.Context
import android.widget.Button
import android.widget.ImageButton
import androidx.core.widget.doAfterTextChanged
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.cas.common.extensions.onClick
import com.cas.musicplayer.R
import com.cas.musicplayer.utils.Utils
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.analytics.FirebaseAnalytics

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/24/20.
 ***************************************
 */

fun Context.writeFeedback() {
    val dialog = MaterialDialog(this)
    dialog.show {
        cancelOnTouchOutside(false)
        cancelable(false)
        customView(viewRes = R.layout.layout_write_feedback, noVerticalPadding = true)
    }
    val btnSend = dialog.view.findViewById<Button>(R.id.btnSend)
    btnSend.isEnabled = false
    val editFeedback = dialog.view.findViewById<TextInputEditText>(R.id.editFeedback)
    editFeedback.doAfterTextChanged {
        btnSend.isEnabled = editFeedback.text?.isNotEmpty() ?: false
    }
    dialog.view.findViewById<ImageButton>(R.id.btnCloseDialog).onClick {
        analytics.logEvent(ANALYTICS_KEY_CLICK_CANCEL_FEEDBACK, null)
        dialog.dismiss()
    }
    btnSend.onClick {
        val feedback = editFeedback.text?.toString() ?: ""
        analytics.logEvent(ANALYTICS_KEY_CLICK_SEND_FEEDBACK, null)
        dialog.dismiss()
        Utils.sendEmail(this, feedback)
    }
    dialog.view.findViewById<Button>(R.id.btnCancel).onClick {
        analytics.logEvent(ANALYTICS_KEY_CLICK_CANCEL_FEEDBACK, null)
        dialog.dismiss()
    }
    analytics.logEvent(ANALYTICS_KEY_CLICK_OPEN_FEEDBACK, null)
}

// Keys feedback
private val ANALYTICS_KEY_CLICK_OPEN_FEEDBACK = "open_feedback_dialog"
private val ANALYTICS_KEY_CLICK_SEND_FEEDBACK = "send_feedback"
private val ANALYTICS_KEY_CLICK_CANCEL_FEEDBACK = "cancel_feedback_dialog"

private val Context.analytics: FirebaseAnalytics
    get() = FirebaseAnalytics.getInstance(this.applicationContext)