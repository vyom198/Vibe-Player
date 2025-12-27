package com.vs.vibeplayer.main.presentation.player.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import com.vs.vibeplayer.core.theme.hover

@Composable
fun PlaybackControls(
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Spacer(modifier = Modifier.width(7.dp))
        IconButton(
            onClick = onPreviousClick,
            modifier = Modifier.size(56.dp).background(
                color = MaterialTheme.colorScheme.hover,
                 shape = CircleShape),

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
            modifier = Modifier.size(56.dp)
                .background(color = MaterialTheme.colorScheme.hover,
                    shape = CircleShape),

        ) {
            Icon(
                painter = painterResource(R.drawable.skip_next),
                contentDescription = "Next",
                tint = MaterialTheme.colorScheme.secondary
            )
        }
        Spacer(modifier = Modifier.width(7.dp))
    }
}