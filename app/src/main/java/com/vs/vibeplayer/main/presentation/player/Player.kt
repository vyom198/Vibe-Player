package com.vs.vibeplayer.main.presentation.player

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.vs.vibeplayer.R
import com.vs.vibeplayer.core.theme.DarkBlueGrey28
import com.vs.vibeplayer.core.theme.DarkSlateGrey
import com.vs.vibeplayer.core.theme.bodyMediumRegular
import com.vs.vibeplayer.core.theme.bodySmallRegular
import com.vs.vibeplayer.core.theme.hover
import com.vs.vibeplayer.main.presentation.components.CreatePlaylistBottomSheet
import com.vs.vibeplayer.main.presentation.components.ObserveAsEvents
import com.vs.vibeplayer.main.presentation.player.components.PlaybackControls
import com.vs.vibeplayer.main.presentation.player.components.PlaylistForBS
import com.vs.vibeplayer.main.presentation.playlist.PlaylistEvent
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

@Composable
fun PlayerRoot(
    viewModel: PlayerViewModel = koinViewModel (),
    navigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    BackHandler {
        navigateBack.invoke()
    }
    ObserveAsEvents(
        flow = viewModel.events
    ) {event ->
        when(event) {
            is PlaylistEvent.OnCreateChannel -> {
                scope.launch {
                    snackbarHostState.showSnackbar(
                            message = if(event.isExists)"Playlist already exists" else "Added to playlist ${event.title}",
                            duration = SnackbarDuration.Short
                        )

                    }


            }

            is PlayerEvent.OnSongAdd -> {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = if(event.isExists)"Song already exists in  ${event.title}" else "Added to playlist ${event.title}",
                        duration = SnackbarDuration.Short)

                }
            }

            is PlayerEvent.OnAddedToFavourites ->{
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = if(event.isAdded) "Added to favourites" else "Already in Favourites",
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

          PlayerScreen(
              state = state,
              onAction = viewModel::onAction,
              NavigateBack =navigateBack,
              snackbarHostState = snackbarHostState
          )


}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    state: PlayerUIState,
    onAction: (PlayerAction) -> Unit,
    NavigateBack : ()->Unit,
    snackbarHostState: SnackbarHostState

) {

    val modalsheet = rememberModalBottomSheetState ()
    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        },
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onAction(PlayerAction.BackPressed)
                            NavigateBack.invoke()
                        },
                        modifier = Modifier

                    ) {
                        Icon(
                             painter = painterResource(id = R.drawable.chevron_down),
                            contentDescription = "collapse"
                        )
                    }
                },
                actions = {

                    IconButton(
                        modifier = Modifier.size(36.dp).clip(
                            shape = CircleShape
                        ).background(
                            color = MaterialTheme.colorScheme.hover ,
                            shape = CircleShape

                        ),
                        onClick = {
                            onAction(PlayerAction.onPlaylistIconClick)
                        }
                    ){
                        Icon(painter = painterResource(
                            id = R.drawable.playlist
                        ),contentDescription = null ,
                            tint = Color.Unspecified)
                    }
                    Spacer(modifier = Modifier.width(16.dp))

                    IconButton(
                            modifier = Modifier.size(36.dp).clip(
                                shape = CircleShape
                            ).background(
                                color = MaterialTheme.colorScheme.hover ,
                                shape = CircleShape

                            ),
                    onClick = {
                     onAction(PlayerAction.ToggleFavourite)
                    }
                    ){
                    Icon(painter = painterResource(
                        id = if(state.isFavourite) R.drawable.heart_filled else R.drawable.heart_outlined
                    ),contentDescription = null ,
                        tint = Color.Unspecified)
                }
                    Spacer(modifier = Modifier.width(8.dp))
                }
            )
        }
    )
    { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {


            Spacer(modifier = Modifier.height(70.dp))
            AsyncImage(
                model = state.currentSong?.cover?.toUri(),
                error = painterResource(id = R.drawable.song_img),
                placeholder = painterResource(id = R.drawable.song_img),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(320.dp)
                    .clip(
                        shape = RoundedCornerShape(4.dp)
                    ),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = state.currentSong?.title ?: "Unknown Title",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = state.currentSong?.artist ?: "Unknown Artist",
                style = MaterialTheme.typography.bodyMediumRegular,
                color = MaterialTheme.colorScheme.secondary
            )





            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp, end = 10.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Spacer(modifier = Modifier.height(19.dp))

                val progressFraction =
                    if (state.duration > 0) state.currentPosition.toFloat() / state.duration else 0f

                BoxWithConstraints(modifier = Modifier) {
                    val fullWidth = maxWidth
                    Slider(
                        value = progressFraction,
                        modifier = Modifier.fillMaxWidth(),
                        onValueChange = {

                            onAction(PlayerAction.OnSeek((it * state.duration).toLong()))
                        },
                        track = { sliderState ->
                            Box(
                                modifier = Modifier
                                    .requiredWidth(fullWidth)
                                    .height(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            color = DarkBlueGrey28,
                                            shape = RoundedCornerShape(50)
                                        )
                                )

                                // Active (filled) track
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(fraction = progressFraction)
                                        .height(6.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            shape = RoundedCornerShape(50)
                                        )
                                )
                            }
                        },
                        thumb = {
                            Box(
                                modifier = Modifier
                                    .padding(bottom = 16.dp) // Space between thumb and track
                            ) {
                                Text(
                                    text = "${state.currentDuration} / ${state.totalDuration}",
                                    style = MaterialTheme.typography.bodySmallRegular,
                                    color = MaterialTheme.colorScheme.surface,
                                    modifier = Modifier.background(
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                )
                            }
                        },
                        valueRange = 0f..1f

                    )
                }


                Spacer(modifier = Modifier.height(20.dp))

                PlaybackControls(
                    isPlaying = state.isPlaying,
                    onPlayPauseClick = { onAction(PlayerAction.PlayOrPause) },
                    onPreviousClick = { onAction(PlayerAction.Previous) },
                    onNextClick = { onAction(PlayerAction.Next) },
                    onShuffleClick = { onAction(PlayerAction.ShuffleClick) },
                    onRepeatClick = { onAction(PlayerAction.OnRepeatClick) },
                    repeatType = state.repeatType,
                    isShuffleEnabled = state.isShuffleEnabled
                )

                Spacer(modifier = Modifier.height(17.dp))
            }
        }

        if(state.isCreateBottomSheetVisible){
            CreatePlaylistBottomSheet(
                onDismiss = { onAction(PlayerAction.onCloseCreateBs) },
                modalState = rememberModalBottomSheetState(),
                onValueChange = {
                    onAction(PlayerAction.onValueChange(it))
                },
                onCreate = {title->
                    onAction(PlayerAction.onCreatePlaylist(title))
                },
                title = state.playlistTitle,
            )
        }
        if (state.isBottomSheetShowing) {
        ModalBottomSheet(
            dragHandle = null,
            onDismissRequest = { onAction(PlayerAction.onDismissBs) },
            sheetState = modalsheet,
            containerColor = MaterialTheme.colorScheme.surface,
        ) {

            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .fillMaxWidth().height(386.dp),

                ) {
                Row(
                    modifier = Modifier.fillMaxWidth().height(88.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                        Icon(
                            painter = painterResource(id = R.drawable.playlist_icon),
                            contentDescription = null,
                            modifier = Modifier.size(64.dp).clickable{
                                onAction(PlayerAction.OnCreateIconClick)
                            },
                            tint = Color.Unspecified
                        )

                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Create Playlist",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    color = DarkSlateGrey
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth().padding(end = 4.dp)
                        .height(88.dp).clickable{
                            onAction(PlayerAction.OnFavouriteClicked)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = R.drawable.favourite_playlist,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        contentScale = ContentScale.Fit

                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Favourites", color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "${state.favouriteSongssize} Songs", color = MaterialTheme.colorScheme.secondary,
                            style = MaterialTheme.typography.bodyMediumRegular
                        )

                    }


                }
               if(state.playlists.isNotEmpty()){
                   PlaylistForBS(
                       playlists = state.playlists ,
                       onPlaylistClick = {playlistId->
                           onAction(PlayerAction.onPlaylistItemClicked(playlistId))
                       }
                   )
               }

            }

        }
    }

    }


}


