package com.vs.vibeplayer.app.navigation

import kotlinx.serialization.Serializable

sealed interface NavigationRoute {
    @Serializable
    data object Permission

    @Serializable
     data object VibePlayer


    @Serializable
    data object ScanScreen
    @Serializable
    data class PlayerScreen(val trackId: Long? =null)

    @Serializable
    data object SearchScreen


}