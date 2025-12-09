package com.vs.vibeplayer.app.navigation

import kotlinx.serialization.Serializable

sealed interface NavigationRoute {
    @Serializable
    data object Permission

    @Serializable
     data object VibePlayer


}