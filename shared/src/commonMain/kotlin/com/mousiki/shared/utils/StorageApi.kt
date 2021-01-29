package com.mousiki.shared.utils

import com.mousiki.shared.fs.PathComponent

interface StorageApi {
    suspend fun downloadFile(
        remoteUrl: String,
        path: PathComponent,
        connectivityState: ConnectivityChecker,
        logErrorMessage: String = "Cannot load $remoteUrl file from firebase"
    ): PathComponent
}