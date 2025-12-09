package com.vs.vibeplayer.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.vs.vibeplayer.main.presentation.VibePlayer.VibePlayerRoot
import com.vs.vibeplayer.main.presentation.permission.PermissionScreen

@Composable
fun NavigationRoot(
    navController: NavHostController,
    isPermissionAlreadyGranted: Boolean
) {
    NavHost(
        navController = navController,
        startDestination = if(isPermissionAlreadyGranted) NavigationRoute.VibePlayer else NavigationRoute.Permission

    ) {
        composable<NavigationRoute.Permission> {
            PermissionScreen(
                onNavigateToScan = {
                    navController.navigate(NavigationRoute.VibePlayer){
                        popUpTo(NavigationRoute.Permission) {
                            inclusive = true
                        }
                        launchSingleTop
                    }

                }
            )


        }
        composable<NavigationRoute.VibePlayer> {
            VibePlayerRoot()
        }

    }
}

