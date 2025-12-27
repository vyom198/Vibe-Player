package com.vs.vibeplayer.main.presentation.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.vs.vibeplayer.R
import com.vs.vibeplayer.core.theme.bodyMediumRegular
import com.vs.vibeplayer.core.theme.hover
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
                            .padding(start = 4.dp)
                            .clip(shape = CircleShape)
                            .background(
                                color = MaterialTheme.colorScheme.hover
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    )
    { paddingValues ->

        Timber.d("${state.currentSong}")
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
                    error = painterResource(id = R.drawable.item_placeholder),
                    placeholder = painterResource(id = R.drawable.item_placeholder),
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
                modifier = Modifier.weight(1f).padding(start=10.dp,end=10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Spacer(modifier = Modifier.height(19.dp))
                LineProgressBar(progress = state.progress)
                Spacer(modifier = Modifier.height(20.dp))

                PlaybackControls(
                    isPlaying = state.isPlaying,
                    onPlayPauseClick = {onAction(PlayerAction.PlayOrPause)},
                    onPreviousClick = {onAction(PlayerAction.Previous)},
                    onNextClick = {onAction(PlayerAction.Next)},
                )

                Spacer(modifier = Modifier.height(17.dp))
            }
        }

    }


}

