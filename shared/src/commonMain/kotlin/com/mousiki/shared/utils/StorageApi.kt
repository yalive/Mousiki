package com.mousiki.shared.utils

interface StorageApi {
    suspend fun downloadFile(
        remoteUrl: String,
        localFile: String,
        connectivityState: ConnectivityChecker,
        logErrorMessage: String
    ): String
}