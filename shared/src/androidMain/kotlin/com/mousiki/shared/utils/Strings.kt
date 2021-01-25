package com.mousiki.shared.utils

actual fun String.format(vararg args: Any): String {
    return String.format(this, args)
}