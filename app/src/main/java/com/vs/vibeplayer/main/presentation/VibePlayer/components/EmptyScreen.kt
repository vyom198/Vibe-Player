package com.vs.vibeplayer.main.presentation.VibePlayer.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vs.vibeplayer.R
import com.vs.vibeplayer.core.buttons.VButton
import com.vs.vibeplayer.core.theme.VibePlayerTheme
import com.vs.vibeplayer.core.theme.bodyMediumRegular

@Composable
fun EmptyScreen(
    modifier: Modifier = Modifier,
    onScanAgain : () -> Unit
) {
    Column(modifier= modifier.fillMaxSize(),
           horizontalAlignment = Alignment.CenterHorizontally,
           verticalArrangement = Arrangement.Center
            ){

        Text(  text = stringResource(R.string.no_music_found),
              style = MaterialTheme.typography.titleLarge,
              color = MaterialTheme.colorScheme.onPrimary)
        Text(  text = stringResource(R.string.try_scanning_again),
            style = MaterialTheme.typography.bodyMediumRegular,
            color = MaterialTheme.colorScheme.secondary)
        Spacer(modifier =Modifier.height(4.dp))
        VButton(text = stringResource(R.string.scan_again),
            onClick = onScanAgain)
    }
}


//@Preview
//@Composable
//private fun EmptyScreenPreview() {
//    VibePlayerTheme {
//        EmptyScreen()
//
//    }
//
//}