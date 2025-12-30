package com.vs.vibeplayer.main.presentation.search

sealed interface SearchAction {
    data class OnSearchTextChange(val query : String) : SearchAction
    data class OnTrackClick(val trackId : Long) : SearchAction
    object OnCrossClick : SearchAction



}