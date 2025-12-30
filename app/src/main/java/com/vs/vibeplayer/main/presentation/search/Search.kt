package com.vs.vibeplayer.main.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vs.vibeplayer.R
import com.vs.vibeplayer.core.theme.DarkBlueGrey28
import com.vs.vibeplayer.core.theme.bodyLargeMedium
import com.vs.vibeplayer.core.theme.bodyLargeRegular
import com.vs.vibeplayer.core.theme.bodyMediumRegular
import com.vs.vibeplayer.core.theme.hover
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber


@Composable
fun SearchRoot(
    viewModel: SearchViewModel = koinViewModel(),
    onCancelClick: () -> Unit,
    onNavigateTrackId : (Long) ->Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val searchText by viewModel.searchText.collectAsStateWithLifecycle()

    SearchScreen(
        state = state,
        onAction = viewModel::onAction,
        onCancelClick = onCancelClick,
        searchText = searchText,
        onNavigateTrackId = onNavigateTrackId

    )
}

@Composable
fun SearchScreen(
    state: SearchState,
    searchText : String ,
    onAction: (SearchAction) -> Unit,
    onNavigateTrackId: (Long) -> Unit,
    onCancelClick: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current


    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }
    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(paddingValues = paddingValues).padding(top = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var searchTerm by remember { mutableStateOf(searchText) }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchTerm,
                    onValueChange = {
                       searchTerm = it
                        onAction(SearchAction.OnSearchTextChange(it))
                    },
                    placeholder = {
                        Text(
                            text = stringResource(R.string.search),
                            style = MaterialTheme.typography.bodyLargeRegular,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.search),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    },
                    trailingIcon = {
                        if (searchText.isNotEmpty()) {
                            Icon(
                                painter = painterResource(R.drawable.x),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.clickable {
                                    searchTerm = ""
                                    onAction(SearchAction.OnCrossClick)
                                },

                                )
                        }
                    },
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .weight(2f).focusRequester(
                            focusRequester
                        ),
                    shape = CircleShape,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DarkBlueGrey28,
                        unfocusedBorderColor = DarkBlueGrey28,
                        focusedContainerColor = MaterialTheme.colorScheme.hover,
                        unfocusedContainerColor = MaterialTheme.colorScheme.hover,
                        focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                    )
                )

                    TextButton(

                        onClick = onCancelClick,
                    ) {
                        Text(
                            text = stringResource(R.string.cancel),
                            style = MaterialTheme.typography.bodyLargeMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }


            }
            Spacer(Modifier.size(10.dp))
            when {
                state.isLoading -> {
                    Timber.d("${state}")
                    Column(modifier = Modifier.fillMaxSize(),
                         horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top

                    ) {
                        CircularProgressIndicator()

                    }

                }

                state.searchResults.isEmpty() -> {
                    Text(

                        text = "No results found",
                        style = MaterialTheme.typography.bodyMediumRegular,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp)) {
                        items(state.searchResults) {
                            SearchResultItem(
                                item = it,
                                onTrackClick = onNavigateTrackId
                            )
                            HorizontalDivider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp,
                                color = DarkBlueGrey28
                            )
                        }


                    }
                }
            }

        }
    }
}



//@Composable
//@Preview
//fun SearchViewPreview() {
//    VibePlayerTheme {
//        SearchScreen(
//            state = SearchState(),
//            searchText = "506",
//            onAction = {},
//            onCancelClick = {}
//
//        )
//
//    }
//
//}