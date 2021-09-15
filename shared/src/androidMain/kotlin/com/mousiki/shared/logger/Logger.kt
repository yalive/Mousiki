package com.mousiki.shared.logger

import android.util.Log

actual fun logd(tag: String, message: String) {
    Log.d(tag, message)
}