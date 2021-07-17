package com.mousiki.shared.utils

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter

actual class KMPDate actual constructor(formatString: String) {

    private val dateFormatter = NSDateFormatter().apply {
        this.dateFormat = formatString
    }

    actual fun asString(): String {
        return dateFormatter.stringFromDate(NSDate())
    }
}