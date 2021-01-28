package com.mousiki.shared.utils

fun List<String>.getOrEmpty(index: Int): String {
    return getOrElse(index) { "" }
}