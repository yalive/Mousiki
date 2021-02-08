package com.mousiki.shared.utils

fun TextResource.asString(): String {
    return when (this) {
        is SimpleTextResource -> text
        is IdTextResource -> error("Not supported in iOS")
        is PluralTextResource -> error("Not supported in iOS")
    }
}