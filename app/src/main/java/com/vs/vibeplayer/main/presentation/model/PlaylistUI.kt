package com.vs.vibeplayer.main.presentation.model

data class PlaylistUI(
    val id : Long,
    val title : String,
    val trackIds : List<Long>? = null,
    val coverArt : String? = null


)
