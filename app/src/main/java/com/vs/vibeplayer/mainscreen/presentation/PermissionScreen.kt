package com.vs.vibeplayer.mainscreen.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vs.vibeplayer.R
import com.vs.vibeplayer.core.buttons.VButton


@Composable
fun PermissionScreen(modifier: Modifier,
                     onAllowClick: () -> Unit ) {
    Column(modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,
           verticalArrangement = Arrangement.Center
        ) {
        Image(painter = painterResource(R.drawable.vlogo),
             contentDescription = null,

            )
        Text(text = stringResource(R.string.vibe_player),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Text(text = stringResource(R.string.permission_rationale),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center

        )
        Spacer(modifier = Modifier.height(16.dp))
        VButton(text = stringResource(R.string.allow_access),
                 onClick = onAllowClick
                 )


    }
}

//@Preview(showBackground = true)
//@Composable
//private fun PermissionPreview() {
//    VibePlayerTheme {
//        PermissionScreen(modifier = Modifier)
//    }
//}