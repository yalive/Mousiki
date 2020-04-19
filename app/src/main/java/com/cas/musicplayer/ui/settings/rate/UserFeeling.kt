package com.cas.musicplayer.ui.settings.rate

import android.content.Context
import android.widget.Button
import android.widget.ImageButton
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.cas.common.extensions.onClick
import com.cas.musicplayer.R
import com.cas.musicplayer.utils.UserPrefs
import com.cas.musicplayer.utils.Utils
import com.cas.musicplayer.utils.longToast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.analytics.FirebaseAnalytics

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/19/20.
 ***************************************
 */
fun Context.askUserForFeelingAboutApp() {
    val dialog = MaterialDialog(this)
    dialog.show {
        customView(viewRes = R.layout.layout_question_user_feeling, noVerticalPadding = true)
    }
    dialog.view.findViewById<ImageButton>(R.id.btnCloseDialog).onClick {
        dialog.dismiss()
    }
    dialog.view.findViewById<Button>(R.id.btnHappy).onClick {
        analytics.logEvent(ANALYTICS_KEY_CLICK_HAPPY, null)
        askUserToRateApp()
        dialog.dismiss()
    }
    dialog.view.findViewById<Button>(R.id.btnMixedFeelings).onClick {
        analytics.logEvent(ANALYTICS_KEY_CLICK_MIXED_FEELING, null)
        letUserWriteComment()
        dialog.dismiss()
    }
    dialog.view.findViewById<Button>(R.id.btnNotHappy).onClick {
        analytics.logEvent(ANALYTICS_KEY_CLICK_NOT_HAPPY, null)
        letUserWriteComment()
        dialog.dismiss()
    }
}

fun Context.askUserToRateApp() {
    val dialog = MaterialDialog(this)
    dialog.show {
        customView(viewRes = R.layout.dialog_rate, noVerticalPadding = true)
    }
    dialog.view.findViewById<ImageButton>(R.id.btnCloseDialog).onClick {
        dialog.dismiss()
    }
    dialog.view.findViewById<Button>(R.id.btnOk).onClick {
        UserPrefs.setRatedApp()
        analytics.logEvent(ANALYTICS_KEY_CLICK_OK_RATE, null)
        Utils.openInPlayStore(this)
        dialog.dismiss()
    }
    dialog.view.findViewById<Button>(R.id.btnCancel).onClick {
        analytics.logEvent(ANALYTICS_KEY_CLICK_CANCEL_RATE, null)
        dialog.dismiss()
    }
}

fun Context.letUserWriteComment() {
    val dialog = MaterialDialog(this)
    dialog.show {
        customView(viewRes = R.layout.layout_write_comment, noVerticalPadding = true)
    }
    val btnSend = dialog.view.findViewById<Button>(R.id.btnOk)
    btnSend.isEnabled = false
    val editComment = dialog.view.findViewById<TextInputEditText>(R.id.editComment)
    editComment.doAfterTextChanged {
        btnSend.isEnabled = editComment.text?.isNotEmpty() ?: false
    }
    dialog.view.findViewById<ImageButton>(R.id.btnCloseDialog).onClick {
        dialog.dismiss()
    }
    btnSend.onClick {
        val bundle = bundleOf(
            "feedback" to editComment.text?.toString()
        )
        analytics.logEvent(ANALYTICS_KEY_CLICK_SEND_COMMENT, bundle)
        dialog.dismiss()
        longToast(getString(R.string.message_feedback_sent))
    }
    dialog.view.findViewById<Button>(R.id.btnLater).onClick {
        analytics.logEvent(ANALYTICS_KEY_CLICK_COMMENT_LATER, null)
        dialog.dismiss()
    }
}

private val Context.analytics: FirebaseAnalytics
    get() = FirebaseAnalytics.getInstance(this.applicationContext)

// Question
private val ANALYTICS_KEY_CLICK_HAPPY = "rate_click_happy"
private val ANALYTICS_KEY_CLICK_MIXED_FEELING = "rate_click_mixed_feeling"
private val ANALYTICS_KEY_CLICK_NOT_HAPPY = "rate_click_not_happy"

// Rate
private val ANALYTICS_KEY_CLICK_OK_RATE = "rate_click_ok_rate"
private val ANALYTICS_KEY_CLICK_CANCEL_RATE = "rate_click_cancel_rate"

// Comment
private val ANALYTICS_KEY_CLICK_COMMENT_LATER = "rate_click_comment_later"
private val ANALYTICS_KEY_CLICK_SEND_COMMENT = "rate_click_send_comment"