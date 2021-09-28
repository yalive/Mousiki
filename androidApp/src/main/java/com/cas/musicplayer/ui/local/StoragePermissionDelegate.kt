package com.cas.musicplayer.ui.local

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.cas.common.extensions.onClick
import com.cas.musicplayer.databinding.LayoutNoStoragePermissionBinding
import com.cas.musicplayer.utils.readStoragePermissionsGranted
import java.lang.ref.WeakReference

interface StoragePermissionDelegate {

    fun registerForActivityResult(
        fragment: Fragment,
        mainView: View,
        permissionView: LayoutNoStoragePermissionBinding
    )

    fun checkStoragePermission(
        onUserGrantPermission: () -> Unit
    )
}

class StoragePermissionDelegateImpl : StoragePermissionDelegate {

    private lateinit var mainViewRef: WeakReference<View>
    private lateinit var permissionViewRef: WeakReference<LayoutNoStoragePermissionBinding>
    private lateinit var mPermissionResult: ActivityResultLauncher<String>
    private lateinit var fragmentRef: WeakReference<Fragment>
    private var shouldShowRequestPermissionRationale = true

    override fun registerForActivityResult(
        fragment: Fragment,
        mainView: View,
        permissionView: LayoutNoStoragePermissionBinding
    ) {
        this.mainViewRef = WeakReference(mainView)
        this.permissionViewRef = WeakReference(permissionView)
        this.fragmentRef = WeakReference(fragment)

        permissionViewRef.get()?.btnAllowPermission?.onClick {
            shouldShowRequestPermissionRationale = shouldShowRequestPermissionRationale(
                fragmentRef.get()!!.requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            mPermissionResult.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        mPermissionResult = fragmentRef.get()
            ?.registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                if (!it) {
                    val perResult = shouldShowRequestPermissionRationale(
                        fragmentRef.get()!!.requireActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                    if (!perResult && !shouldShowRequestPermissionRationale) {
                        openAppSettings()
                    }
                }
            } as ActivityResultLauncher<String>
    }

    override fun checkStoragePermission(onUserGrantPermission: () -> Unit) {

        val readStoragePermissionsGranted = fragmentRef.get()?.readStoragePermissionsGranted()

        if (readStoragePermissionsGranted != null && readStoragePermissionsGranted) {
            permissionViewRef.get()?.root?.isVisible = false
            mainViewRef.get()?.isVisible = true
            onUserGrantPermission()
        } else {
            permissionViewRef.get()?.root?.isVisible = true
            mainViewRef.get()?.isVisible = false
        }
    }

    private fun openAppSettings() {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val context = mainViewRef.get()?.context ?: return
            val uri = Uri.fromParts("package", context.packageName, null)
            intent.data = uri
            context.startActivity(intent)
        } catch (e: Exception) {
        }
    }
}
