package com.mousiki.shared.utils

import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

actual val elapsedRealtime: Long
    get() {
        val ms = NSDate().timeIntervalSince1970 * 1000
        return ms.toLong()
    }