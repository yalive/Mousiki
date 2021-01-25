package com.mousiki.shared.utils

import com.mousiki.shared.PluralRes
import com.mousiki.shared.StringRes

sealed class TextResource {
    companion object {
        fun fromText(text: String): TextResource = SimpleTextResource(text)
        fun fromStringId(@StringRes id: Int): TextResource = IdTextResource(id)
        fun fromPlural(@PluralRes id: Int, pluralValue: Int): TextResource =
            PluralTextResource(id, pluralValue)
    }
}

data class SimpleTextResource(
    val text: String
) : TextResource()

// Util for Android
data class IdTextResource(
    @StringRes val id: Int
) : TextResource()

data class PluralTextResource(
    @PluralRes val pluralId: Int,
    val quantity: Int
) : TextResource()