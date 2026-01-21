package com.vs.vibeplayer.main.presentation.addsongs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vs.vibeplayer.R
import com.vs.vibeplayer.core.buttons.VButton
import com.vs.vibeplayer.core.theme.DarkBlueGrey28
import com.vs.vibeplayer.core.theme.VibePlayerTheme
import com.vs.vibeplayer.core.theme.bodyLargeMedium
import com.vs.vibeplayer.core.theme.bodyLargeRegular
import com.vs.vibeplayer.core.theme.bodyMediumRegular
import com.vs.vibeplayer.core.theme.bodySmallRegular
import com.vs.vibeplayer.core.theme.disabled
import com.vs.vibeplayer.core.theme.hover
import com.vs.vibeplayer.main.presentation.addsongs.components.AddSongSearchResultItem
import com.vs.vibeplayer.main.presentation.addsongs.components.CustomCheckBox
import com.vs.vibeplayer.main.presentation.components.ObserveAsEvents
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddSongsRoot(
    viewModel: AddSongsViewModel = koinViewModel(),
    onBackClick: () -> Unit ,

) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val searchText by viewModel.searchText.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    ObserveAsEvents(flow = viewModel.events){ event ->
       when(event){
           AddSongEvent.onInsertEvent -> {
               scope.launch {

                   snackbarHostState.showSnackbar(
                       message = "${state.selectedIds.size} songs added to playlist.",
                      duration = SnackbarDuration.Short
                   )

               }
               onBackClick.invoke()

           }
       }

    }
    AddSongsScreen(
        state = state,
        onAction = viewModel::onAction,
        searchText = searchText,
        onBackClick = onBackClick,
        snackbarHostState = snackbarHostState

    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSongsScreen(
    state: AddSongsState,
    onAction: (AddSongsAction) -> Unit,
    searchText: String,
    onBackClick: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val focusManager = LocalFocusManager.current
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
           SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = {
            Button(onClick = {onAction(AddSongsAction.onInsertSong)},

                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = Color.Transparent,
                    disabledContentColor = Color.Transparent

                ),
                shape = CircleShape,
                enabled = state.selectedIds.isNotEmpty(),
                modifier = Modifier.width(383.dp ).height(
                    44.dp
                )) {
                Text(text = "OK")
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if(state.selectedIds.isNotEmpty())"${state.selectedIds.size} Selected" else "Add Songs",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLargeMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .background(
                                color = MaterialTheme.colorScheme.hover,
                                shape = CircleShape
                            )
                            .clip(
                                shape = CircleShape
                            )
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            )


        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically) {

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    value = searchText,
                    onValueChange = {
                        onAction(AddSongsAction.OnTextChange(it))
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DarkBlueGrey28,
                        unfocusedBorderColor = DarkBlueGrey28,
                        focusedContainerColor = MaterialTheme.colorScheme.hover,
                        unfocusedContainerColor = MaterialTheme.colorScheme.hover,
                    ),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.search),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    },
                    placeholder = {
                        Text(
                            text = "Search",
                            style = MaterialTheme.typography.bodyLargeRegular,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    },

                    shape = CircleShape,

                    trailingIcon = {
                        Icon(
                            modifier = Modifier.clickable {
                                onAction(AddSongsAction.OnClearClick)
                            },
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.disabled
                        )

                    }

                )
                Spacer(
                    modifier = Modifier.width(
                        8.dp
                    )
                )
                if (searchText.isNotEmpty()) {
                    TextButton(onClick = {
                        focusManager.clearFocus()
                        onAction(AddSongsAction.OnClearClick)
                    }) {
                        Text(
                            text = "Cancel",
                            style = MaterialTheme.typography.bodyLargeMedium,
                            color = MaterialTheme.colorScheme.primaryContainer
                        )
                    }
                }
            }



            if (state.searchResults.isNotEmpty()) {
                LazyColumn(modifier = Modifier.fillMaxSize(),) {
                    stickyHeader {

                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier
                                        .height(52.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CustomCheckBox(
                                        checked = state.isSelectAll,
                                        onCheckedChange = {
                                            onAction(AddSongsAction.onSelectAll(it))
                                        },

                                        )
                                    Spacer(modifier = Modifier.width(11.dp))
                                    Text(
                                        text = "Select All",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )

                                }
                                HorizontalDivider(thickness = 1.dp, color = DarkBlueGrey28)

                            }


                    }

                    items(state.searchResults) {

                        AddSongSearchResultItem(item = it ,
                            onToggleSelection = { id , isSelected ->
                                onAction(AddSongsAction.onToggleClickbyItem(id = id , isSelected = isSelected))
                            }
                            )
                        HorizontalDivider(thickness = 1.dp, color = DarkBlueGrey28)
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(11.dp))
                Column(modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally){
                    Text(
                        text = "No Results Found",
                        style =
                            MaterialTheme.typography.bodyMediumRegular,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

            }


        }


    }

}


