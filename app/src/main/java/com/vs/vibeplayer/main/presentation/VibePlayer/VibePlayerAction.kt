package com.vs.vibeplayer.main.presentation.VibePlayer



sealed interface VibePlayerAction {
    data object onScanAgain : VibePlayerAction


    data object onScanButton : VibePlayerAction

    data  class onDurationSelect(val seconds: Int) : VibePlayerAction

    data class onSizeSelect(val kb: Int) : VibePlayerAction

    data object  onPlayClick  : VibePlayerAction
    data object  shuffleClick  : VibePlayerAction

    data object  onUpdatingPlayingState : VibePlayerAction

}