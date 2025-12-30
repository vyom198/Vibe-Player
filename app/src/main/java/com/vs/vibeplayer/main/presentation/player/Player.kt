package com.vs.vibeplayer.main.presentation.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.vs.vibeplayer.R
import com.vs.vibeplayer.core.theme.DarkBlueGrey28
import com.vs.vibeplayer.core.theme.VibePlayerTheme
import com.vs.vibeplayer.core.theme.bodyMediumRegular
import com.vs.vibeplayer.core.theme.bodySmallRegular
import com.vs.vibeplayer.main.presentation.player.components.LineProgressBar
import com.vs.vibeplayer.main.presentation.player.components.PlaybackControls
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

@Composable
fun PlayerRoot(
    viewModel: PlayerViewModel = koinViewModel (),
    navigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    PlayerScreen(
        state = state,
        onAction = viewModel::onAction,
        NavigateBack =navigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    state: PlayerUIState,
    onAction: (PlayerAction) -> Unit,
    NavigateBack : ()->Unit

) {

    Scaffold(
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
                    model = state.currentSong?.cover,
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
                    text = state.currentSong?.title ?: "Unknown Title", style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = state.currentSong?.artist ?: "Unknown Artist", style = MaterialTheme.typography.bodyMediumRegular,
                    color = MaterialTheme.colorScheme.secondary
                )





            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp, end = 10.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Spacer(modifier = Modifier.height(19.dp))

                var sliderValue = remember(state.currentPosition){
                    derivedStateOf {
                        state.currentPositionFraction

                    }.value
                }



                Slider(
                    value = sliderValue,
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = {
                       sliderValue = it
                    },
                    onValueChangeFinished = {
                        onAction(PlayerAction.OnSeek((sliderValue* state.duration).toLong()))

                    },
                  track = { sliderState ->
                      Box(
                          modifier = Modifier
                              .weight(1f)
                              .height(6.dp) // Thin track height
                      ) {
                          // Inactive (background) track
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
                                  .fillMaxWidth(fraction = sliderState.value)
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
                Spacer(modifier = Modifier.height(20.dp))

                PlaybackControls(
                    isPlaying = state.isPlaying,
                    onPlayPauseClick = {onAction(PlayerAction.PlayOrPause)},
                    onPreviousClick = {onAction(PlayerAction.Previous)},
                    onNextClick = {onAction(PlayerAction.Next)},
                    onShuffleClick = {onAction(PlayerAction.ShuffleClick)},
                    onRepeatClick = {onAction(PlayerAction.OnRepeatClick)},
                    repeatType = state.repeatType,
                    isShuffleEnabled = state.isShuffleEnabled
                )

                Spacer(modifier = Modifier.height(17.dp))
            }
        }

    }


}

//@Preview
//@Composable
//private fun PlayerScreenPrev() {
//    VibePlayerTheme {
//        PlayerScreen(
//            state = PlayerUIState(),
//            onAction = {},
//            NavigateBack = {}
//        )
//    }
//
//}

