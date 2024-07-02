package com.hypelist.presentation.permission

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class PermissionChecker(
    private val activity: ComponentActivity
): DefaultLifecycleObserver {

    init {
        activity.lifecycle.addObserver(this)
    }

    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    var callback: () -> Unit = {}

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        val request = ActivityResultContracts.RequestPermission()
        permissionLauncher = activity.registerForActivityResult(request) { isGranted ->
            callback()
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        permissionLauncher.unregister()
    }

    fun requestPermission(permission: String) {
        permissionLauncher.launch(permission)
    }
}