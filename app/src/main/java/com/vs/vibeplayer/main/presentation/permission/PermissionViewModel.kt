package com.vs.vibeplayer.main.presentation.permission

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PermissionViewModel(
    private val prefs: SharedPreferences
) : ViewModel() {

    companion object {
        private const val PERMISSION_GRANTED_KEY = "permission_granted"
    }
    private val _permissionState = MutableStateFlow<Boolean?>(null)
    val permissionState = _permissionState.asStateFlow()
    private val _showDialog = MutableStateFlow<Boolean>(false)
    val showDialog =_showDialog.asStateFlow()
    fun checkPermissionStatus(context: Context) {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        val isGranted = ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED

        _permissionState.value = isGranted
    }
    fun onPermissionResult(
        isGranted: Boolean
    ) {
        if(isGranted) {
            _permissionState.value = true
            prefs.edit().putBoolean(PERMISSION_GRANTED_KEY, true).apply()
        }else{
            showDialog()
        }
    }

    fun hideDialog() {
        _showDialog.value = false
    }
    fun showDialog(){
        _showDialog.value = true
    }


}