package com.vs.vibeplayer.main.presentation.miniplayer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.vs.vibeplayer.R
import com.vs.vibeplayer.core.theme.DarkSlateGrey
import com.vs.vibeplayer.core.theme.LightSteelBlue
import com.vs.vibeplayer.core.theme.VibePlayerTheme
import com.vs.vibeplayer.core.theme.bodyMediumRegular
import com.vs.vibeplayer.main.presentation.player.components.LineProgressBar
import org.koin.androidx.compose.koinViewModel

@Composable
fun MiniPlayerRoot(
    viewModel: MiniPlayerViewModel = koinViewModel(),
    onMiniPlayerClick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    MiniPlayerScreen(
        state = state,
        onAction = viewModel::onAction,
        onMiniPlayerClick = onMiniPlayerClick
    )
}

@Composable
fun MiniPlayerScreen(
    state: MiniPlayerState,
    onAction: (MiniPlayerAction) -> Unit,
    onMiniPlayerClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth().height(150.dp)
            .background(
                color = DarkSlateGrey,
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp
                )
            )
            .padding(horizontal = 16.dp).padding(
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            ).clickable{
                onMiniPlayerClick.invoke()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Album Art
        AsyncImage(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
            model = state.currentSong?.cover,
            error = painterResource(R.drawable.song_cover_small),
            placeholder = painterResource(R.drawable.song_cover_small),
            contentDescription = null
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Content Column
        Column(
            modifier = Modifier
                .weight(1f).height(65.dp)

        ) {
            // Top row with song info and controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Song Info
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Top
                ) {

                        Text(
                            text = state.currentSong?.title ?: "Unknown Title",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                    Text(
                        text = state.currentSong?.artist?: "Unknown artist",
                        style = MaterialTheme.typography.bodyMediumRegular,
                        color = LightSteelBlue,
                        maxLines = 1,
                        overflow = TextOverflow.Visible
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Play/Pause Button
                IconButton(
                    onClick = { onAction(MiniPlayerAction.PlayOrPause) },
                    modifier = Modifier.size(44.dp).background(
                        color = MaterialTheme.colorScheme.onPrimary,
                        shape = CircleShape
                    )
                ) {
                    Icon(
                        painter = if (state.isPlaying)
                            painterResource(R.drawable.pause)
                        else
                            painterResource(R.drawable.play),
                        contentDescription = if (true) "Pause" else "Play",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.background
                    )
                }

                // Next Button
                IconButton(
                    onClick = { onAction(MiniPlayerAction.Next)},
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.skip_next),
                        contentDescription = "Next",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Progress Bar
            Spacer(modifier = Modifier.height(14.dp))
            LineProgressBar(
                progress =  state.progress,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}




//@Preview
//@Composable
//private fun Preview() {
//    VibePlayerTheme {
//        MiniPlayerScreen(
//            state = MiniPlayerState(),
//
//            onAction = {},
//            onMiniPlayerClick = {},
//
//
//        )
//    }
//}