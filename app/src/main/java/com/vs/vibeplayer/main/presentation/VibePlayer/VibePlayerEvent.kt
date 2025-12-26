package com.vs.vibeplayer.main.presentation.VibePlayer

interface VibePlayerEvent {
    data class ScanCompleted(val scanCompleted : Boolean , val  SongCount : Int) : VibePlayerEvent
}