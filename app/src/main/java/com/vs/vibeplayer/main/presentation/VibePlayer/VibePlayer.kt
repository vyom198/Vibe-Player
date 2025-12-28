package com.vs.vibeplayer.main.presentation.VibePlayer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vs.vibeplayer.R
import com.vs.vibeplayer.core.theme.AlmostBlack
import com.vs.vibeplayer.core.theme.SlateGrey
import com.vs.vibeplayer.core.theme.VibePlayerTheme
import com.vs.vibeplayer.core.theme.accent
import com.vs.vibeplayer.core.theme.bodyLargeMedium
import com.vs.vibeplayer.core.theme.bodyMediumRegular
import com.vs.vibeplayer.core.theme.hover
import com.vs.vibeplayer.main.presentation.VibePlayer.components.AudioList
import com.vs.vibeplayer.main.presentation.VibePlayer.components.EmptyScreen
import com.vs.vibeplayer.main.presentation.components.Loader
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

@Composable
fun VibePlayerRoot(
    viewModel: VibePlayerViewModel = koinViewModel(),
    NavigateToScanScreen : () -> Unit,
    NavigateWithTrackId : (Long) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    VibePlayerScreen(
        state = state,
        onAction = viewModel::onAction,
        onScanClick = NavigateToScanScreen,
        NavigateWithTrackId = NavigateWithTrackId
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VibePlayerScreen(
    state: VibePlayerState,
    onScanClick : () -> Unit,
    onAction: (VibePlayerAction) -> Unit,
    NavigateWithTrackId : (Long) -> Unit
) {
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
         TopAppBar(

             title = {
                 Row(modifier = Modifier.wrapContentSize(),
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.Start){

                     Icon(
                         painter = painterResource(R.drawable.vlogo),
                         contentDescription = null,
                         tint = MaterialTheme.colorScheme.accent
                     )
                     Text(text = stringResource(R.string.vibe_player),
                         style =  MaterialTheme.typography.bodyLargeMedium,
                         color =  MaterialTheme.colorScheme.accent)
                 }


             },
             actions = {
                 IconButton(onClick = onScanClick,
                     modifier = Modifier
                         .padding(end = 4.dp)
                         .background(
                             color = MaterialTheme.colorScheme.hover, shape = CircleShape
                         ))
                          {
                     Icon(
                         painter = painterResource(R.drawable.scan),
                         contentDescription = null,

                     )
                 }
             }

         )

        },
        floatingActionButton = {
            IconButton(
                onClick = {
                    if(state.trackList.isNotEmpty()){
                        coroutineScope.launch {
                            lazyListState.animateScrollToItem(0)
                        }
                    }

                },
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrow_up),
                    contentDescription = "scrolltoUp",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        },

    ) { paddingValues ->
        Column(modifier = Modifier
            .padding(paddingValues)
            .padding(start = 16.dp, end = 16.dp, top = 16.dp) ,
            horizontalAlignment = Alignment.CenterHorizontally ,
             verticalArrangement = Arrangement.Center) {

            when{
                state.scanning ->{
                   Column (modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,
                       verticalArrangement = Arrangement.Center) {
                        Loader(isScannigInMainScreen = state.scanning)
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(text = stringResource(R.string.scanning_your_device_for_music),
                            style = MaterialTheme.typography.bodyMediumRegular,
                            color = MaterialTheme.colorScheme.secondary)
                    }

                }

                state.trackList.isEmpty() -> {
                    EmptyScreen(
                        onScanAgain = {
                            onAction(VibePlayerAction.onScanAgain)
                        }
                    )

                }

                else ->{
                    AudioList(audioList = state.trackList,
                              state = lazyListState,
                             onTrackClick = NavigateWithTrackId)
                    Timber.d("${state.trackList.size}")
                }
            }


        }

    }



}

//        @Preview
//        @Composable
//        private fun Preview() {
//            VibePlayerTheme {
//                VibePlayerScreen(
//                    state = VibePlayerState(),
//                    onAction = {},
//                    onScanClick = {},
//                    NavigateWithTrackId = {}
//                )
//            }
//        }
