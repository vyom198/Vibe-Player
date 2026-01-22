package com.vs.vibeplayer.main.presentation.playlistDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

class PlaylistDetailViewModel : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(PlaylistDetailState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                /** Load initial data here **/
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = PlaylistDetailState()
        )

    fun onAction(action: PlaylistDetailAction) {
        when (action) {
            else -> TODO("Handle actions")
        }
    }

}