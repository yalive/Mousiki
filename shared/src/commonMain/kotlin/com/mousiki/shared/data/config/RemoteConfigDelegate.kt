package com.mousiki.shared.data.config

interface RemoteConfigDelegate {
    fun fetchAndActivate(completionHandler: (Boolean) -> Unit)
    fun getBoolean(key: String): Boolean
    fun getString(key: String): String
    fun getInt(key: String): Int
}