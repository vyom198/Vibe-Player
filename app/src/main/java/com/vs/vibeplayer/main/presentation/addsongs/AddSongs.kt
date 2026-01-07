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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vs.vibeplayer.R
import com.vs.vibeplayer.core.theme.DarkBlueGrey28
import com.vs.vibeplayer.core.theme.VibePlayerTheme
import com.vs.vibeplayer.core.theme.bodyLargeMedium
import com.vs.vibeplayer.core.theme.bodyLargeRegular
import com.vs.vibeplayer.core.theme.bodySmallRegular
import com.vs.vibeplayer.core.theme.disabled
import com.vs.vibeplayer.core.theme.hover
import com.vs.vibeplayer.main.presentation.search.SearchAction
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddSongsRoot(
    viewModel: AddSongsViewModel = koinViewModel(),
    onBackClick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val searchText by viewModel.searchText.collectAsStateWithLifecycle()
    AddSongsScreen(
        state = state,
        onAction = viewModel::onAction,
        searchText = searchText,
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSongsScreen(
    state: AddSongsState,
    onAction: (AddSongsAction) -> Unit,
    searchText : String,
    onBackClick : () -> Unit
) {
    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                 title = {
                     Text(text = "Add Songs",
                         textAlign = TextAlign.Center,
                         style = MaterialTheme.typography.bodyLargeMedium,
                         color = MaterialTheme.colorScheme.onPrimary)
                 },
                 navigationIcon = {
                     IconButton(onClick = onBackClick,
                          modifier = Modifier.padding(start = 10.dp).background(
                              color = MaterialTheme.colorScheme.hover,
                              shape = CircleShape
                          ).clip(
                              shape = CircleShape
                          ).size(36.dp)){
                         Icon(
                             imageVector = Icons.Default.ArrowBack ,
                             contentDescription = null,
                             modifier = Modifier.size(16.dp),
                             tint = MaterialTheme.colorScheme.secondary
                         )
                     }
                 }
             )


           }
        ) {paddingValues ->

            Column(modifier = Modifier.padding(paddingValues).padding(horizontal = 16.dp).fillMaxSize()) {
             var searchText by remember { mutableStateOf(searchText) }
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                    value = searchText,
                    onValueChange = {
                        searchText = it
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
                           painter =  painterResource(R.drawable.search),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    },
                    placeholder ={
                        Text(text = "Search",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLargeRegular,
                            color = MaterialTheme.colorScheme.secondary)
                    },

                    shape = CircleShape,

                    trailingIcon = {
                        Icon(
                            modifier = Modifier.clickable{
                                searchText = ""
                                onAction(AddSongsAction.OnClearClick)
                            },
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.disabled
                        )

                    }

                )

                if(state.searchResults.isNotEmpty()){
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(modifier = Modifier.height(52.dp).fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically) {

                                    Checkbox(
                                        checked = false,
                                        onCheckedChange = null,


                                        modifier = Modifier.size(28.dp).border(
                                            width = 1.dp,
                                            color = DarkBlueGrey28,
                                            shape = CircleShape

                                        ).clip(
                                            CircleShape
                                        ),
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = MaterialTheme.colorScheme.primary,
                                            uncheckedColor = MaterialTheme.colorScheme.secondary,
                                            checkmarkColor = Color.White

                                        )

                                    )
                                    Spacer(modifier = Modifier.width(11.dp))
                                    Text(text = "Select All",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onPrimary)

                                }
                                HorizontalDivider(thickness = 1.dp , color = DarkBlueGrey28)

                            }

                        }
                        items(state.searchResults){

                            AddSongSearchResultItem(item = it)
                            HorizontalDivider(thickness = 1.dp , color = DarkBlueGrey28)
                        }
                    }
                }else{
                    Text(text = "No Results Found",
                        textAlign = TextAlign.Center,
                        style =
                        MaterialTheme.typography.bodySmallRegular,
                        color = MaterialTheme.colorScheme.secondary)
                }


        }



    }

}

@Preview
@Composable
private fun Preview() {
    VibePlayerTheme {
        AddSongsScreen(
            state = AddSongsState(),
            onAction = {},
            searchText = "",
            onBackClick = {}
        )
    }
}