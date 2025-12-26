package com.vs.vibeplayer.main.presentation.VibePlayer

import com.vs.vibeplayer.main.presentation.model.AudioTrackUI


sealed interface VibePlayerAction {
    data object onScanAgain : VibePlayerAction
    data object onFabClicked : VibePlayerAction

    data object onScanButton : VibePlayerAction
    data class onTrackClicked(val trackUI: AudioTrackUI) : VibePlayerAction

   data  class onDurationSelect(val seconds: Int) : VibePlayerAction

    data class onSizeSelect(val kb: Int) : VibePlayerAction


}