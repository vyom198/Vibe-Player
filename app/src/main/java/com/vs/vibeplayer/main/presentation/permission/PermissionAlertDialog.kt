package com.vs.vibeplayer.main.presentation.permission

import androidx.compose.foundation.clickable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.vs.vibeplayer.R

@Composable
fun PermissionDialog(
    onTryAgain: () -> Unit,
    onOk: () -> Unit,
    onDismiss: () -> Unit
) {
  AlertDialog(
      onDismissRequest = onDismiss,
      confirmButton =  {
          Text(text = "Try Again",
              modifier = Modifier.clickable{
                  onTryAgain.invoke()
              })
      },
      dismissButton = {
          Text(text = "OK" , modifier = Modifier.clickable{
             onOk.invoke()
          })
      },
      title = {
          Text(text = stringResource(R.string.permission_required))
      },
      text = {
          Text(text = stringResource(R.string.permission_needed),

              )
      }

  )
}

@Preview
@Composable
private fun PermissionDialogPreview() {
  PermissionDialog(onOk ={},
       onDismiss = {} ,
       onTryAgain = {})
}