package com.cas.musicplayer.utils

import android.app.Activity
import android.util.Log
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.cas.musicplayer.R
import com.cas.musicplayer.di.Injector
import com.cas.musicplayer.ui.settings.InAppUpdateProgressDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.common.IntentSenderForResultStarter
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.crashlytics.FirebaseCrashlytics


class InAppUpdateManager(
    private val activity: FragmentActivity,
    @IdRes private val snakeBarRootViewRes: Int
) {
    private val analytics by lazy { Injector.analytics }
    private val appUpdateManager by lazy { AppUpdateManagerFactory.create(activity) }
    private val updateLauncher = activity.registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) {
        Log.d(TAG, "registerForActivityResult got result")
        handleActivityResult(it.resultCode)
    }

    private val progressDialog by lazy {
        InAppUpdateProgressDialog.present(activity.supportFragmentManager)
    }

    private val inAppUpdateListener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADING) {
            val percent = if (state.totalBytesToDownload() == 0L) 0
            else state.bytesDownloaded() * 100 / state.totalBytesToDownload()
            progressDialog.setProgress(percent.toInt())
        }

        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            Log.d(TAG, "Update DOWNLOADED")
            analytics.logEvent(ANALYTICS_UPDATE_DOWNLOADED)
            progressDialog.dismiss()
            // After the update is downloaded, show a notification
            // and request user confirmation to restart the app.
            popupSnackBarForCompleteUpdate()
        }
    }

    fun init() {
        activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) = checkUpdate()

            override fun onResume(owner: LifecycleOwner) = checkNonInstalledUpdate()
            override fun onStop(owner: LifecycleOwner) {
                appUpdateManager.unregisterListener(inAppUpdateListener)
            }
        })
    }

    fun checkUpdate(automaticCheck: Boolean = true) {
        Log.d(TAG, "checking update...")
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            Log.d(TAG, "checkUpdate: ${appUpdateInfo.updateAvailability()}")
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                Log.d(TAG, "UPDATE_AVAILABLE, newVersion=${appUpdateInfo.availableVersionCode()}")
                PreferenceUtil.lastVersion = appUpdateInfo.availableVersionCode()

                if (automaticCheck) {
                    val days = appUpdateInfo.clientVersionStalenessDays() ?: -1
                    if (days in 1..4) return@addOnSuccessListener
                    if (!PreferenceUtil.canShowUpdateDialog) return@addOnSuccessListener
                }

                // Request the update.
                val senderResult = IntentSenderForResultStarter { intentSender, _, _, _, _, _, _ ->
                    updateLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
                }
                try {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.FLEXIBLE,
                        senderResult,
                        IN_APP_UPDATE_RQ
                    )
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                }
            } else {
                Log.d(TAG, "UPDATE NOT AVAILABLE")
            }
        }
        appUpdateManager.registerListener(inAppUpdateListener)
    }

    private fun handleActivityResult(resultCode: Int) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                Log.d(TAG, "onActivityResult Ok")
                analytics.logEvent(ANALYTICS_UPDATE_STARTED)
            }
            Activity.RESULT_CANCELED -> {
                Log.d(TAG, "onActivityResult KO")
                analytics.logEvent(ANALYTICS_UPDATE_CANCELED)
                PreferenceUtil.canShowUpdateDialog = false
            }
            ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {
                Log.d(TAG, "onActivityResult in app failed")
                analytics.logEvent(ANALYTICS_UPDATE_FAILED)
            }
        }
    }

    private fun checkNonInstalledUpdate() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            // If the update is downloaded but not installed,
            // notify the user to complete the update.
            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackBarForCompleteUpdate()
            }
        }
    }

    private fun popupSnackBarForCompleteUpdate() {
        Snackbar.make(
            activity.findViewById(snakeBarRootViewRes),
            R.string.in_app_update_installed,
            Snackbar.LENGTH_INDEFINITE
        ).apply {
            setAction(R.string.in_app_update_restart_app) { appUpdateManager.completeUpdate() }
            setActionTextColor(activity.color(R.color.colorAccent))
            show()
        }
    }

    companion object {
        private const val IN_APP_UPDATE_RQ = 100
        private const val TAG = "inAppUpdate"

        // Analytics
        private const val ANALYTICS_UPDATE_CANCELED = "app_update_canceled"
        private const val ANALYTICS_UPDATE_FAILED = "app_update_failed"
        private const val ANALYTICS_UPDATE_STARTED = "app_update_started"
        private const val ANALYTICS_UPDATE_DOWNLOADED = "app_update_downloaded"
    }
}