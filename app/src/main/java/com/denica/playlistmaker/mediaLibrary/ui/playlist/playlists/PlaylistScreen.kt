package com.denica.playlistmaker.mediaLibrary.ui.playlist.playlists

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.denica.playlistmaker.R
import com.denica.playlistmaker.mediaLibrary.domain.Playlist
import com.denica.playlistmaker.mediaLibrary.ui.playlist.playlists.PlaylistViewModel.Companion.pluralizeTracks
import com.denica.playlistmaker.utils.Loading
import com.denica.playlistmaker.utils.NothingFound


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlaylistsScreen(
    playlistState: PlaylistState,
    onPlaylistClick: (Playlist) -> Unit,
    onAddPlaylistClick: () -> Unit
) {


    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            onClick = onAddPlaylistClick,
            modifier = Modifier.padding(top = 24.dp, bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                stringResource(R.string.media_library_button_add_new_playlist),
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }

        when (playlistState) {
            is PlaylistState.Loading -> {
                Loading()
            }

            is PlaylistState.Empty -> {
                NothingFound(R.string.media_library_playlists_empty)
            }

            is PlaylistState.Content -> {

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(playlistState.data) { playlist ->
                        PlaylistItem(playlist) { onPlaylistClick(playlist) }
                    }
                }
            }
        }
    }
}

@Composable
fun PlaylistItem(
    playlist: Playlist,
    modifier: Modifier = Modifier,
    onClick: ((Playlist) -> Unit)
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
            .clickable { onClick(playlist) }

    ) {


        AsyncImage(
            model = playlist.imagePath,
            contentDescription = null,
            placeholder = painterResource(R.drawable.ic_track_placeholder),
            error = painterResource(R.drawable.ic_track_placeholder),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(8.dp))
        )


        Text(
            text = playlist.name,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontFamily = FontFamily(Font(R.font.ys_display_regular)),
            fontSize = 12.sp,
            lineHeight = 16.sp,
            color = MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier.padding(top = 4.dp)
        )


        Text(
            text =
                String.format(
                    stringResource(
                        pluralizeTracks(
                            playlist.trackCount
                        )
                    ), playlist.trackCount
                ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontFamily = FontFamily(Font(R.font.ys_display_regular)),
            fontSize = 12.sp,
            lineHeight = 16.sp,
            color = MaterialTheme.colorScheme.onSecondary
        )
    }
}