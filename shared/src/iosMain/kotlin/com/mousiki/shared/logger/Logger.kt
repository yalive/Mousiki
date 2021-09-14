package com.mousiki.shared.logger

import platform.Foundation.NSLog

actual fun logd(tag: String, message: String) {
    NSLog("$tag: $message")
}