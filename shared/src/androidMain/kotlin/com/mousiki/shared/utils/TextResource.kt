package com.mousiki.shared.utils

import android.content.Context

fun Context.resolve(textResource: TextResource): String {
    return when (textResource) {
        is SimpleTextResource -> textResource.text
        is IdTextResource -> resources.getString(textResource.id)
        is PluralTextResource -> resources.getQuantityString(
            textResource.pluralId,
            textResource.quantity
        )
    }
}