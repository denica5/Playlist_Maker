package com.denica.playlistmaker.search.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.denica.playlistmaker.R
import com.denica.playlistmaker.main.ui.theme.LocalAppColors
import com.denica.playlistmaker.search.domain.models.Song
import com.denica.playlistmaker.utils.Loading
import com.denica.playlistmaker.utils.NothingFound
import com.denica.playlistmaker.utils.TrackLazyColumn
import com.denica.playlistmaker.utils.TrackListColumn

@Composable
fun SearchScreen(
    uiState: SearchState,
    onQueryChange: (String) -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    onClearQuery: () -> Unit,
    onRefreshClick: () -> Unit,
    onClearHistoryClick: () -> Unit,
    textFieldState: TextFieldState,
    searchHistoryState: SearchHistoryState,
    onSearchSongClickDebounce: (Song) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.search_text_header),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                backgroundColor = MaterialTheme.colorScheme.primary,
                elevation = 0.dp
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(color = MaterialTheme.colorScheme.primary)
        ) {

            SearchInput(
                query = textFieldState.query,
                showClear = textFieldState.isShowClearIc,
                onQueryChange = onQueryChange,
                onClearQuery = onClearQuery,
                onFocusChanged = onFocusChanged,
                searchHistoryState = searchHistoryState,
                showHistoryTitle = textFieldState.isShowHistory,
                onClearHistoryClick = onClearHistoryClick,
                onSearchSongClickDebounce = onSearchSongClickDebounce
            )

            when (uiState) {
                is SearchState.Loading -> {
                    Loading()
                }

                is SearchState.Error -> {
                    FailedSearch(
                        onRefreshClick = onRefreshClick
                    )
                }

                is SearchState.Empty -> {
                    NothingFound(R.string.nothing_found)
                }

                is SearchState.Content -> {
                    TrackListColumn(
                        tracks = uiState.data,
                        onSearchSongClickDebounce = onSearchSongClickDebounce,
                    )
                }

                is SearchState.Nothing -> {

                }
            }
        }
    }
}

@Composable
private fun SearchInput(
    query: String,
    showClear: Boolean,
    showHistoryTitle: Boolean,
    searchHistoryState: SearchHistoryState,
    onClearHistoryClick: () -> Unit,
    onQueryChange: (String) -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    onClearQuery: () -> Unit,
    onSearchSongClickDebounce: (Song) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Box(

        ) {

            TextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        onFocusChanged(focusState.isFocused)
                    },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                placeholder = {
                    Text(
                        text = stringResource(R.string.search_hint_edit_text),
                        color = LocalAppColors.current.editTextHint,

                        )
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_search_search_layout),
                        contentDescription = null,
                        tint = LocalAppColors.current.icSearchIcon,
                    )
                },
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    unfocusedContainerColor = LocalAppColors.current.editTextBackground,
                    focusedContainerColor = LocalAppColors.current.editTextBackground,
                    cursorColor = LocalAppColors.current.editTextCursor,
                    selectionColors = TextSelectionColors(
                        LocalAppColors.current.editTextCursor,
                        LocalAppColors.current.editTextCursorBackground
                    )

                ),
                textStyle = TextStyle(
                    fontWeight = MaterialTheme.typography.bodySmall.fontWeight,
                    fontFamily = MaterialTheme.typography.bodySmall.fontFamily,
                    fontSize = MaterialTheme.typography.bodySmall.fontSize,
                    color = LocalAppColors.current.editTextText
                ),

                )

            if (showClear) {
                IconButton(
                    onClick = onClearQuery,
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_cancel_x),
                        contentDescription = null,
                        tint = LocalAppColors.current.icClearSearch

                    )
                }
            }
        }

        if (showHistoryTitle) {
            when (searchHistoryState) {
                is SearchHistoryState.Empty -> {

                }

                is SearchHistoryState.Content -> {
                    Text(
                        text = stringResource(R.string.you_search),
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 24.dp, bottom = 8.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 19.sp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    TrackLazyColumn(
                        searchHistoryState.data, onSearchSongClickDebounce, Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )
                    Button(
                        onClick = onClearHistoryClick,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.clear_history),
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun FailedSearch(
    onRefreshClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.ic_failed_search),
            contentDescription = null,
            modifier = Modifier.padding(top = 102.dp)
        )

        Text(
            text = stringResource(R.string.failed_search),
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 19.sp,

            )

        Button(
            onClick = onRefreshClick,
            modifier = Modifier.padding(top = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                text = stringResource(R.string.button_refresh),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp,
            )
        }
    }
}