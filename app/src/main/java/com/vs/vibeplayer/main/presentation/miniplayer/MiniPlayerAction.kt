package com.vs.vibeplayer.main.presentation.miniplayer

import com.vs.vibeplayer.main.presentation.player.PlayerAction

sealed interface MiniPlayerAction {
    object PlayOrPause :MiniPlayerAction
    object Stop : MiniPlayerAction
    object Next : MiniPlayerAction


}