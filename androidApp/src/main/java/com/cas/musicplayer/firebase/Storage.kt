package com.cas.musicplayer.firebase

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.mousiki.shared.fs.PathComponent
import com.mousiki.shared.utils.ConnectivityChecker
import com.mousiki.shared.utils.StorageApi
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class Storage(
    private val firebaseStorage: FirebaseStorage
) : StorageApi {

    override suspend fun downloadFile(
        remoteUrl: String,
        path: PathComponent,
        connectivityState: ConnectivityChecker,
        logErrorMessage: String
    ): PathComponent {
        val file = File(path.component!!)
        val localFile = downloadFileFormFirebaseStorage(
            remoteUrl = remoteUrl,
            localFile = file,
            connectivityState = connectivityState,
            logErrorMessage = logErrorMessage
        )
        return PathComponent(localFile.absolutePath)
    }

    private suspend fun downloadFileFormFirebaseStorage(
        remoteUrl: String,
        localFile: File,
        connectivityState: ConnectivityChecker,
        logErrorMessage: String = "Cannot load $remoteUrl file from firebase"
    ): File {
        if (!localFile.exists()) {
            val connectedBeforeCall = connectivityState.isConnected()
            var retryCount = 0
            var fileDownloaded = false
            var fileExist = true
            while (retryCount < MAX_RETRY_FIREBASE_STORAGE && !fileDownloaded && fileExist) {
                retryCount++
                fileDownloaded = suspendCoroutine { continuation ->
                    val ref = firebaseStorage.getReferenceFromUrl(remoteUrl)
                    Log.d("StorageApi", "Calling $remoteUrl")
                    ref.getFile(localFile).addOnSuccessListener {
                        Log.d("StorageApi", "Success $remoteUrl")
                        continuation.resume(true)
                    }.addOnFailureListener {
                        Log.d("StorageApi", "Failure $remoteUrl with exception $it")
                        if ((it as? StorageException)?.errorCode == StorageException.ERROR_OBJECT_NOT_FOUND) {
                            fileExist = false
                        }
                        continuation.resume(false)
                    }
                }
            }
            if (!fileDownloaded) {
                // Log error
                FirebaseCrashlytics.getInstance().log(
                    "$logErrorMessage ==> after $retryCount retries," +
                            "\n Is Connected before call: $connectedBeforeCall" +
                            "\n Is Connected after call:${connectivityState.isConnected()}"
                )
            }
        }
        return localFile
    }
}

private val MAX_RETRY_FIREBASE_STORAGE = 4