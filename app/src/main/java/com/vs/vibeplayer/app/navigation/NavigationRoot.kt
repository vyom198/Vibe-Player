package com.vs.vibeplayer.app.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.vs.vibeplayer.main.presentation.VibePlayer.VibePlayerRoot
import com.vs.vibeplayer.main.presentation.permission.PermissionScreen
import com.vs.vibeplayer.main.presentation.VibePlayer.scan.ScanRoot
import com.vs.vibeplayer.main.presentation.addsongs.AddSongsRoot
import com.vs.vibeplayer.main.presentation.miniplayer.MiniPlayerRoot
import com.vs.vibeplayer.main.presentation.player.PlayerRoot
import com.vs.vibeplayer.main.presentation.playlist.PlaylistRoot
import com.vs.vibeplayer.main.presentation.playlistDetail.PlaylistDetailRoot
import com.vs.vibeplayer.main.presentation.search.SearchRoot

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
                    navController.navigate(NavigationRoute.VibePlayer) {
                        popUpTo(NavigationRoute.Permission) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }

                }
            )


        }
        composable<NavigationRoute.VibePlayer> {
            VibePlayerRoot(
                NavigateToScanScreen = {
                    navController.navigate(NavigationRoute.ScanScreen) {
                        launchSingleTop = true

                    }
                },
                NavigateWithTrackId = { trackId ->
                    navController.navigate(NavigationRoute.PlayerScreen(trackId)) {
                        launchSingleTop = true

                    }

                },
                onSearchClick = {
                    navController.navigate(NavigationRoute.SearchScreen) {
                        launchSingleTop = true
                    }
                },
                onShuffleClick = {
                    navController.navigate(NavigationRoute.PlayerScreen()) {
                        launchSingleTop = true
                    }
                },
                onPlayClick = {
                    navController.navigate(NavigationRoute.PlayerScreen()) {
                        launchSingleTop = true
                    }
                },
                onMiniPlayerClick = {
                    navController.navigate(NavigationRoute.PlayerScreen()) {
                        launchSingleTop = true
                    }
                },

                onCreateClick = {
                    navController.navigate(NavigationRoute.AddSongs(it)) {
                        launchSingleTop = true
                    }
                },
                onNavigatetoPlayer = {
                    navController.navigate(NavigationRoute.PlayerScreen()) {
                        popUpTo(NavigationRoute.VibePlayer) {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                },
                OnNavigateToPlaylistDetail = {
                    navController.navigate(NavigationRoute.PlaylistDetail(it)){
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<NavigationRoute.ScanScreen> {
            ScanRoot(
                onBackClick = {
                    navController.navigate(NavigationRoute.VibePlayer) {
                        popUpTo(NavigationRoute.ScanScreen) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onMainScreen = {
                    navController.popBackStack()
                }
            )

        }


        composable<NavigationRoute.PlayerScreen>(

            enterTransition = {
                slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing)
                )
            },

            exitTransition = {
                slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing)
                )

            },

        ) {
            PlayerRoot(
                navigateBack = {
                    navController.navigate(NavigationRoute.VibePlayer){
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<NavigationRoute.SearchScreen> {
            SearchRoot(
                onCancelClick = {
                    navController.popBackStack()
                },
                onNavigateTrackId = {
                    navController.navigate(NavigationRoute.PlayerScreen(it)) {
                        launchSingleTop = true
                    }
                }
            )

        }



        composable <NavigationRoute.AddSongs>{
            AddSongsRoot(
                onBackClick = {
                    navController.popBackStack()
                },



            )
        }

        composable<NavigationRoute.PlaylistDetail> {
            PlaylistDetailRoot()

        }

    }
}

