package com.cas.musicplayer.ui.local.songs

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.lifecycle.MutableLiveData
import com.cas.common.extensions.onClick
import com.cas.musicplayer.databinding.LayoutNoStoragePermissionBinding
import com.cas.musicplayer.tmp.observe
import com.cas.musicplayer.utils.readStoragePermissionsGranted

interface StoragePermissionDelegate {

    fun checkStoragePermission(
        mainView: View,
        permissionView: LayoutNoStoragePermissionBinding,
        onUserGrantPermission: () -> Unit
    )

    /**
     * Fragment should call this method when got permission result
     */
    fun onRequestPermissionsResultDelegate(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    )
}

class StoragePermissionDelegateImpl : StoragePermissionDelegate {

    lateinit var mainView: View
    lateinit var permissionView: LayoutNoStoragePermissionBinding

    private var notified = false

    override fun checkStoragePermission(
        mainView: View,
        permissionView: LayoutNoStoragePermissionBinding,
        onUserGrantPermission: () -> Unit
    ) {
        this.mainView = mainView
        this.permissionView = permissionView
        val fragment = permissionView.root.findFragment<Fragment>()
        permissionView.btnAllowPermission.onClick {
            fragment.requestPermissions(READ_STORAGE_PERMISSION, REQ_CODE_STORAGE_PERMISSION)
        }

        val readStoragePermissionsGranted = fragment.readStoragePermissionsGranted()
        mainView.isVisible = readStoragePermissionsGranted
        permissionView.root.isVisible = !readStoragePermissionsGranted
        fragment.observe(StoragePermissionGranted) {
            if (notified) return@observe
            onUserGrantPermission()
            notified = true
            permissionView.root.isVisible = false
            mainView.isVisible = true
        }
    }

    override fun onRequestPermissionsResultDelegate(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        if (requestCode == REQ_CODE_STORAGE_PERMISSION && grantResults.first() == PackageManager.PERMISSION_GRANTED) {
            permissionView.root.isVisible = false
            mainView.isVisible = true
            StoragePermissionGranted.value = Unit
        } else if (grantResults.first() == PackageManager.PERMISSION_DENIED) {
            try {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val context = mainView.context
                val uri: Uri = Uri.fromParts("package", context.packageName, null)
                intent.data = uri
                context.startActivity(intent)
            } catch (e: Exception) {
            }
        }
    }

    companion object {
        private const val REQ_CODE_STORAGE_PERMISSION = 12
        private val READ_STORAGE_PERMISSION = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
}

object StoragePermissionGranted : MutableLiveData<Unit>()