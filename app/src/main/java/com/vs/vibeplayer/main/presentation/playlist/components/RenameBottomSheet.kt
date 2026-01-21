package com.vs.vibeplayer.main.presentation.playlist.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vs.vibeplayer.R
import com.vs.vibeplayer.core.theme.bodyLargeMedium
import com.vs.vibeplayer.core.theme.bodyLargeRegular
import com.vs.vibeplayer.core.theme.bodySmallRegular
import com.vs.vibeplayer.core.theme.disabled
import com.vs.vibeplayer.core.theme.hover
import com.vs.vibeplayer.main.presentation.playlist.PlaylistAction
import com.vs.vibeplayer.main.presentation.playlist.PlaylistState
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenameBottomSheet(
    onDismiss : ()-> Unit ,
    state : PlaylistState,
    onValueChange: (String)-> Unit,
    onRenameConfirm : (String) -> Unit
) {
    ModalBottomSheet(
        dragHandle = null,
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .wrapContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = stringResource(R.string.rename_playlist),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.height(18.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                shape = CircleShape,
                value = state.prefilledTitle,
                trailingIcon = {
                    Text(

                        text = "${state.prefilledTitle.length}/40",
                        style = MaterialTheme.typography.bodySmallRegular,
                        color = MaterialTheme.colorScheme.secondary
                    )
                },
                textStyle = MaterialTheme.typography.bodyLargeRegular,
                onValueChange = {
                    val filtered = it.take(40)
                    onValueChange(filtered)

                },
                singleLine = true,
                placeholder = {
                    Text(
                        text = stringResource(R.string.enter_playlist_name),
                        style = MaterialTheme.typography.bodyLargeRegular,

                        )
                },


                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                    focusedBorderColor = MaterialTheme.colorScheme.surface,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surface,
                    unfocusedTextColor = MaterialTheme.colorScheme.secondary,
                    focusedContainerColor = MaterialTheme.colorScheme.hover,
                    unfocusedContainerColor = MaterialTheme.colorScheme.hover,
                )
            )

            Spacer(modifier = Modifier.height(18.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                OutlinedButton(
                    onClick = onDismiss, modifier = Modifier
                        .width(180.dp)
                        .height(44.dp)
                ) {
                    Text(
                        text = stringResource(R.string.cancel),
                        style = MaterialTheme.typography.bodyLargeMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                }
                Spacer(modifier = Modifier.width(8.dp))
                Timber.d("state.currentPlaylist?.title ${state.currentPlaylist?.title}")
                Button(
                    onClick = { onRenameConfirm(state.prefilledTitle) },
                    enabled = state.prefilledTitle != state.currentPlaylist?.title,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        disabledContentColor = MaterialTheme.colorScheme.disabled,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.hover

                    ), modifier = Modifier
                        .fillMaxWidth()
                        .width(180.dp)
                        .height(44.dp)
                ) {
                    Text(
                        text = stringResource(R.string.rename),
                        style = MaterialTheme.typography.bodyLargeMedium,
                    )

                }

            }


        }
    }
}