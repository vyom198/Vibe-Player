package com.vs.vibeplayer.main.presentation.player.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.vs.vibeplayer.R
import com.vs.vibeplayer.core.theme.disabled
import com.vs.vibeplayer.core.theme.hover
import com.vs.vibeplayer.main.presentation.player.RepeatType

@Composable
fun PlaybackControls(
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onShuffleClick: () -> Unit ,
    onRepeatClick: () -> Unit,
    modifier: Modifier = Modifier,
    repeatType: RepeatType ,
    isShuffleEnabled: Boolean
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {


        IconButton(
            onClick = onShuffleClick,
            modifier = Modifier
                .size(56.dp)
                .background(
                    color = if(isShuffleEnabled)
                    { MaterialTheme.colorScheme.hover} else {
                        MaterialTheme.colorScheme.surface
                    },
                    shape = CircleShape
                ),

            ) {
            Icon(
                painter = painterResource(R.drawable.shuffle),
                contentDescription = "shuffle",
                tint = if(isShuffleEnabled)
                       { MaterialTheme.colorScheme.secondary} else {
                    MaterialTheme.colorScheme.disabled
                }
            )
        }
        IconButton(
            onClick = onPreviousClick,
            modifier = Modifier
                .size(56.dp)
                .background(
                    color = MaterialTheme.colorScheme.hover,
                    shape = CircleShape
                ),

        ) {
            Icon(
                painter = painterResource(R.drawable.skip_previous),
                contentDescription = "Previous",
                tint = MaterialTheme.colorScheme.secondary

            )
        }

        IconButton(
            onClick = onPlayPauseClick,
            modifier = Modifier
                .size(72.dp)
                .background(
                    color = MaterialTheme.colorScheme.onBackground,
                    shape = RoundedCornerShape(50)
                )
        ) {
            Icon(
                painter = if (isPlaying) painterResource(R.drawable.pause) else painterResource(R.drawable.play),
                contentDescription = if (isPlaying) "Pause" else "Play",
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.background

            )

        }

        IconButton(
            onClick = onNextClick,
            modifier = Modifier
                .size(56.dp)
                .background(
                    color = MaterialTheme.colorScheme.hover,
                    shape = CircleShape
                ),

        ) {
            Icon(
                painter = painterResource(R.drawable.skip_next),
                contentDescription = "Next",
                tint = MaterialTheme.colorScheme.secondary
            )
        }
        IconButton(
            onClick = onRepeatClick,
            modifier = Modifier
                .size(56.dp)
                .background(
                    color = if(repeatType == RepeatType.OFF)
                    { MaterialTheme.colorScheme.surface} else {
                        MaterialTheme.colorScheme.hover
                    },
                    shape = CircleShape
                ),

            ) {
            Icon(
                painter = when(repeatType){
                    RepeatType.OFF -> painterResource(R.drawable.repeat_off)
                    RepeatType.REPEAT_ONE -> painterResource(R.drawable.repeat_one)
                    RepeatType.REPEAT_ALL -> painterResource(R.drawable.repeat)
                },
                contentDescription = "repeat",
                tint = if(repeatType == RepeatType.OFF)
                    { MaterialTheme.colorScheme.disabled} else {
                    MaterialTheme.colorScheme.secondary
                }
            )
        }

    }
}


//@Preview
//@Composable
//private fun PlaybackControlPreview() {
//    VibePlayerTheme {
//        PlaybackControls(
//            isPlaying = true,
//            onPlayPauseClick = {},
//            onPreviousClick = {},
//            onNextClick = {},
//            onShuffleClick = {},
//            onRepeatClick = {}
//
//        )
//    }
//
//}