package com.mousiki.shared.utils

import java.text.SimpleDateFormat
import java.util.*

actual class KMPDate actual constructor(formatString: String) {
    private val dateFormat = SimpleDateFormat(formatString)
    actual fun asString(): String {
        return dateFormat.format(Date())
    }
}