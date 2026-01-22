package com.vs.vibeplayer.main.presentation.playlist.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vs.vibeplayer.R
import com.vs.vibeplayer.core.theme.ButtonDestructive
import com.vs.vibeplayer.core.theme.bodyLargeMedium
import com.vs.vibeplayer.core.theme.bodyMediumRegular

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteBottomSheet(
    onDismiss : ()-> Unit ,
    id: Long ,
    onDeleteConfirm : (Long) -> Unit,
) {
    ModalBottomSheet(
        dragHandle = null,
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {

        Column(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 16.dp)
                .wrapContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = stringResource(R.string.delete_playlist),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )

            Text(
                text = "Are you sure you want to delete playlist New Playlist?",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMediumRegular,
                color = MaterialTheme.colorScheme.secondary
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
                Button(
                    onClick = { onDeleteConfirm(id) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ButtonDestructive,
                        contentColor = MaterialTheme.colorScheme.onPrimary,

                    ), modifier = Modifier
                        .fillMaxWidth()
                        .width(180.dp)
                        .height(44.dp)
                ) {
                    Text(
                        text = stringResource(R.string.delete),
                        style = MaterialTheme.typography.bodyLargeMedium,
                    )

                }

            }



        }
    }
}