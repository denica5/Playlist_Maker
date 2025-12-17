package com.denica.playlistmaker.mediaLibrary.ui.favouriteTracks

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.denica.playlistmaker.R
import com.denica.playlistmaker.search.domain.models.Song
import com.denica.playlistmaker.utils.Loading
import com.denica.playlistmaker.utils.NothingFound
import com.denica.playlistmaker.utils.TrackLazyColumn

@Composable
fun FavouriteTracksScreen(
    favouriteTracksState: FavouriteTracksState,
    onSongDebounceClick: (Song) -> Unit
) {
    when (favouriteTracksState) {
        is FavouriteTracksState.Loading -> {
            Loading()
        }

        is FavouriteTracksState.Empty -> {
            NothingFound(R.string.media_library_favourite_tracks_empty)
        }

        is FavouriteTracksState.Content -> {
            TrackLazyColumn(favouriteTracksState.data,onSongDebounceClick, Modifier.fillMaxSize())
        }
    }
}