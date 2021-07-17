package com.mousiki.shared.utils

const val DB_DATE_FORMAT = "YYYY-MM-DD HH:MM:SS.SSS"

expect class KMPDate(formatString: String) {
    fun asString(): String
}