package com.vs.vibeplayer.main.presentation.VibePlayer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import org.koin.androidx.compose.koinViewModel

@Composable
fun VibePlayerRoot(
    viewModel: VibePlayerViewModel = koinViewModel(),
    NavigateToScanScreen : () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state) {
        println("DEBUG: Current state - scanning: ${state.scanning}, trackList size: ${state.trackList.size}")
    }
    VibePlayerScreen(
        state = state,
        onAction = viewModel::onAction,
        onScanClick = NavigateToScanScreen
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VibePlayerScreen(
    state: VibePlayerState,
    onScanClick : () -> Unit,
    onAction: (VibePlayerAction) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
         TopAppBar(
             title = {
                 Row(
                     modifier = Modifier.fillMaxWidth(),
                     verticalAlignment = Alignment.CenterVertically,

                 ) {

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

        }
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
                        Loader()
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
                    AudioList(list = state.trackList)

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
//                    onAction = {}
//                )
//            }
//        }
