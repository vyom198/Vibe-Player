package com.vs.vibeplayer.app

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.vs.vibeplayer.app.navigation.NavigationRoot
import com.vs.vibeplayer.core.theme.VibePlayerTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPrefs: SharedPreferences by inject()

        val isPermissionAlreadyGranted = sharedPrefs.getBoolean("permission_granted", false)

        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            VibePlayerTheme {
                NavigationRoot(navController = rememberNavController(),
                    isPermissionAlreadyGranted =isPermissionAlreadyGranted)
            }
        }
    }
}