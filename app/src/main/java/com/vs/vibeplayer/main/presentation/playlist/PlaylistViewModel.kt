package com.vs.vibeplayer.main.presentation.playlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vs.vibeplayer.core.database.playlist.PlaylistDao
import com.vs.vibeplayer.core.database.playlist.PlaylistEntity
import com.vs.vibeplayer.core.database.track.TrackDao
import com.vs.vibeplayer.main.presentation.VibePlayer.VibePlayerEvent
import com.vs.vibeplayer.main.presentation.model.PlaylistUI
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class PlaylistViewModel(
    private val playlistDao: PlaylistDao,

) : ViewModel() {
    private val eventChannel = Channel<PlaylistEvent>()
    val events = eventChannel.receiveAsFlow()
    private var hasLoadedInitialData = false


    private val _state = MutableStateFlow(PlaylistState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                getPlaylists()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = PlaylistState()
        )

    fun getPlaylists(){
        viewModelScope.launch {
            playlistDao.getAllPlaylist().collect { playlists ->
                _state.update {
                    it.copy(
                        isShowing = false,
                        playlists = playlists.map { playlist->
                            PlaylistUI(
                                id = playlist.id,
                                title = playlist.title,
                                 trackIds = playlist.trackIds ,
                                coverArt = playlist.coverArt
                            )
                        }
                    )
                }

            }

        }
    }




    fun insertIfNotExists(title : String) {
        viewModelScope.launch {
            val playlistEntity = PlaylistEntity(
                title = title
               )
            val inserted = playlistDao.insertIfNotExists(playlistEntity)
            if(!inserted){
                _state.update {
                    it.copy(
                        isExists = true,
                        isShowing = false


                    )
                }
                eventChannel.send(PlaylistEvent.OnCreateChannel(true))
            }else{
                _state.update {
                    it.copy(
                        isExists = false,
                        isShowing = false
                    )
                }

                eventChannel.send(PlaylistEvent.OnCreateChannel(isExists = false,
                    title = title
                ))
            }

        }
    }



    fun onAction(action: PlaylistAction) {
        when (action) {
            PlaylistAction.AddIconClickedOrCreatePlaylist -> {
                _state.update {
                    it.copy(
                        isShowing = true
                    )
                }
            }
            PlaylistAction.onDismissSheet ->{

                _state.update {
                    it.copy(
                        isShowing = false ,
                        title =  ""

                    )
                }

            }

            is PlaylistAction.onCreateClick -> {

                insertIfNotExists(action.title)



            }

            PlaylistAction.onSnackBarDismissed -> {
                _state.update {
                    it.copy(
                        isExists = false
                    )
                }

            }

            is PlaylistAction.onTextChange -> {
                _state.update {
                    it.copy(
                        title = action.title
                    )
                }
            }
        }
    }

}