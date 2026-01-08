package com.vs.vibeplayer.main.presentation.playlist

import com.vs.vibeplayer.main.presentation.model.PlaylistUI

data class PlaylistState(
 val isShowing : Boolean = false,
 val isExists : Boolean = false,
 val playlists : List<PlaylistUI> = emptyList() ,

)