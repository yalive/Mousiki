package com.cas.musicplayer.utils

import android.database.Cursor

/**
 * Created by Fayssel Yabahddou on 6/18/21.
 */

internal fun Cursor.getInt(columnName: String): Int {
    try {
        return this.getInt(this.getColumnIndex(columnName))
    } catch (ex: Throwable) {
        throw IllegalStateException("invalid column $columnName", ex)
    }
}

internal fun Cursor.getLong(columnName: String): Long {
    try {
        return this.getLong(this.getColumnIndex(columnName))
    } catch (ex: Throwable) {
        throw IllegalStateException("invalid column $columnName", ex)
    }
}

internal fun Cursor.getString(columnName: String): String {
    try {
        return this.getString(this.getColumnIndex(columnName))
    } catch (ex: Throwable) {
        throw IllegalStateException("invalid column $columnName", ex)
    }
}

internal fun Cursor.getStringOrNull(columnName: String): String? {
    return try {
        getString(columnName)
    } catch (ex: Throwable) {
        null
    }
}
