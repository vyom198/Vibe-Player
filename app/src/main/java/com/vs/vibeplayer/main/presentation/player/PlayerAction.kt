package com.vs.vibeplayer.main.presentation.player

sealed interface PlayerAction {
    object PlayOrPause : PlayerAction
    object Stop : PlayerAction
    object Next : PlayerAction
    object Previous : PlayerAction
    object BackPressed : PlayerAction
}