package com.vs.vibeplayer.main.presentation.VibePlayer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vs.vibeplayer.core.theme.VibePlayerTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun VibePlayerRoot(
    viewModel: VibePlayerViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    VibePlayerScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun VibePlayerScreen(
    state: VibePlayerState,
    onAction: (VibePlayerAction) -> Unit,
) {

}

@Preview
@Composable
private fun Preview() {
    VibePlayerTheme {
        VibePlayerScreen(
            state = VibePlayerState(),
            onAction = {}
        )
    }
}