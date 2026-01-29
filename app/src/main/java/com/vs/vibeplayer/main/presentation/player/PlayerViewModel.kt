package com.vs.vibeplayer.main.presentation.player

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vs.vibeplayer.core.database.playlist.PlaylistDao
import com.vs.vibeplayer.core.database.playlist.PlaylistEntity
import com.vs.vibeplayer.core.database.track.TrackDao
import com.vs.vibeplayer.main.domain.favourite.FavouritePrefs
import com.vs.vibeplayer.main.domain.player.PlayerManager
import com.vs.vibeplayer.main.presentation.model.PlaylistUI
import com.vs.vibeplayer.main.presentation.playlist.PlaylistEvent
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class PlayerViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val playerManager: PlayerManager,
    private val trackDao: TrackDao ,
    private val playlistDao: PlaylistDao,
    private val favouritePrefs: FavouritePrefs
) : ViewModel() {

    private var hasLoadedInitialData = false
    private val trackId = savedStateHandle.get<Long?>("trackId")
    private val eventChannel = Channel<PlayerEvent>()
    val events = eventChannel.receiveAsFlow()
    private val _state = MutableStateFlow(PlayerUIState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                loadSongData(trackId ?: 0L)
                observePlayerState()
                loadPlaylist()
                loadFavouriteSongs()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = PlayerUIState()
        )

    private fun loadFavouriteSongs() {
        viewModelScope.launch {
            favouritePrefs.getfavouriteList().collect { favouriteSongs ->
                if(playerManager.isFavouritePlaying()){
                    val songs = favouriteSongs.map {
                        async {  trackDao.getTrackById(it)!!}
                    }.awaitAll()
                    playerManager.concatMediaSource(songs)
                }
                _state.update {
                    it.copy(
                        favouriteSongs = favouriteSongs
                    )

                }

            }

        }
    }

    private fun loadPlaylist (){

        viewModelScope.launch {
            playlistDao.getAllPlaylist().collect { playlists ->


                _state.update {
                    it.copy(
                        playlists = playlists.map {
                            PlaylistUI(
                                id = it.id,
                                title = it.title,
                                coverArt = it.coverArt,
                                trackIds = it.trackIds
                            )
                        }
                    )
                }
            }
        }
    }
    private fun loadSongData(trackId: Long) {
        viewModelScope.launch {
            try {
                val clickedSong = trackDao.getTrackById(trackId)
                 trackDao.observeTracks().collect {
                    if (clickedSong != null) {
                        playerManager.initialize(clickedSong,  it)
                    }
                }

            } catch (e: Exception) {
                Timber.d(e.message)
            }
        }
    }
    private fun observePlayerState() {
        viewModelScope.launch {

            playerManager.playerState.collect { playerState ->
                _state.value = _state.value.copy(
                    isPlaying = playerState.isPlaying,
                    currentSong = playerState.currentSong,
                    currentPosition = playerState.currentPosition,
                    duration = playerState.duration,
                    canGoNext = playerState.canGoNext,
                    canGoPrevious = playerState.canGoPrevious,
                    repeatType = playerState.repeatType,
                    isShuffleEnabled = playerState.isShuffleEnabled,
                    currentPositionFraction = playerState.currentPositionFraction,
                    isPlayerAvailable = playerState.playerisAvailable

                )
            }
        }

    }
    private fun SongAddtoPlaylist (playlistId : Long ){
        viewModelScope.launch {
            val playlist = playlistDao.getplaylistById(playlistId)
            val isExists = async {   playlist.trackIds?.contains(_state.value.currentSong?.id)?: false}.await()
            if(isExists){
                _state.update {
                    it.copy(
                        isBottomSheetShowing = false
                    )
                }
                eventChannel.send(PlayerEvent.OnSongAdd(playlist.title,
                    isExists = true))
                return@launch
            }
            val newPLaylist = playlist.trackIds?.let {
                playlist.copy(
                    trackIds = it +  _state.value.currentSong?.id!!
                )
            }
            if (newPLaylist != null) {
                playlistDao.insert(newPLaylist)
            }
            _state.update {
                it.copy(
                    isBottomSheetShowing = false
                )
            }
            eventChannel.send(PlayerEvent.OnSongAdd(playlist.title,false ))



        }
    }
    private fun createPlaylist(title : String){
        viewModelScope.launch {
            val playlistEntity = PlaylistEntity(
                title = title,
                coverArt = _state.value.currentSong?.cover,
                trackIds = listOf(_state.value.currentSong?.id ?: 0L)
            )
            val inserted = playlistDao.insertIfNotExists(playlistEntity)
            _state.update {
                it.copy(
                    isCreateBottomSheetVisible = false,
                    isBottomSheetShowing = false,
                )
            }
            if(inserted){
                eventChannel.send(PlayerEvent.OnCreateChannel(isExists = false,
                    title = title
                ))

            }else{
                eventChannel.send(PlayerEvent.OnCreateChannel(isExists = true))
            }
            _state.update {
                it.copy(

                    playlistTitle = ""
                )
            }
        }
    }
    private fun toggleFavourite() {
         viewModelScope.launch {
             _state.value.currentSong?.let {
                 favouritePrefs.toggleFavourite(it.id)
             }
         }
    }

    fun onAction(action: PlayerAction) {
        when (action) {
            PlayerAction.BackPressed -> handleBackPressed()
            PlayerAction.Next -> playerManager.next()
            PlayerAction.PlayOrPause -> {
                if (_state.value.isPlaying) playerManager.pause() else playerManager.play()
            }

            PlayerAction.Previous -> playerManager.previous()
            PlayerAction.Stop -> playerManager.stop()
            PlayerAction.OnRepeatClick -> playerManager.onRepeatClick()
            PlayerAction.ShuffleClick -> playerManager.shuffleSong()
            is PlayerAction.OnSeek -> playerManager.seekTo(action.position)
            PlayerAction.ToggleFavourite -> toggleFavourite()
            PlayerAction.onDismissBs -> {
                _state.value = _state.value.copy(
                    isBottomSheetShowing = false
                )
            }

            PlayerAction.onPlaylistIconClick -> {
                _state.value = _state.value.copy(
                    isBottomSheetShowing = true
                )
            }

            is PlayerAction.onPlaylistItemClicked -> SongAddtoPlaylist(action.playlistId)

            is PlayerAction.onCreatePlaylist -> createPlaylist(action.title)

            PlayerAction.onCloseCreateBs -> {
                _state.update {
                    it.copy(
                        isCreateBottomSheetVisible = false,
                        playlistTitle = ""
                    )
                }
            }

            is PlayerAction.onValueChange -> {
                _state.update {
                    it.copy(
                        playlistTitle = action.value
                    )
                }

            }

            PlayerAction.OnCreateIconClick -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            isCreateBottomSheetVisible = true
                        )
                    }
                }
            }

            PlayerAction.OnFavouriteClicked -> {
                viewModelScope.launch {
                    if(_state.value.isFavourite) {
                        eventChannel.send(PlayerEvent.OnAddedToFavourites(false))
                        _state.update {
                            it.copy(
                                isBottomSheetShowing = false
                            )
                        }
                        return@launch
                    }
                    toggleFavourite()
                    _state.update {
                        it.copy(
                            isBottomSheetShowing = false
                        )
                    }
                    eventChannel.send(PlayerEvent.OnAddedToFavourites(true))
                }

            }
        }
    }
    private fun handleBackPressed() {
        //playerManager.stop()
    }

    override fun onCleared() {
        playerManager.release()
        super.onCleared()
    }

}