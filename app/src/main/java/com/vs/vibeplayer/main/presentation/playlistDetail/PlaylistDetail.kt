package com.vs.vibeplayer.main.presentation.playlistDetail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vs.vibeplayer.core.theme.VibePlayerTheme

@Composable
fun PlaylistDetailRoot(
    viewModel: PlaylistDetailViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    PlaylistDetailScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun PlaylistDetailScreen(
    state: PlaylistDetailState,
    onAction: (PlaylistDetailAction) -> Unit,
) {

}

@Preview
@Composable
private fun Preview() {
    VibePlayerTheme {
        PlaylistDetailScreen(
            state = PlaylistDetailState(),
            onAction = {}
        )
    }
}