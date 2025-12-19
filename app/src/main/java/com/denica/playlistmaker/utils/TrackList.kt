package com.denica.playlistmaker.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.denica.playlistmaker.R
import com.denica.playlistmaker.search.domain.models.Song
import com.denica.playlistmaker.search.ui.SearchFragment.Companion.formatDuration

@Composable
fun TrackListColumn(
    tracks: List<Song>,
    onSearchSongClickDebounce: (Song) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        TrackLazyColumn(
            tracks, onSearchSongClickDebounce, modifier
                .weight(1f)
                .fillMaxWidth()
        )
    }
}

@Composable
fun TrackLazyColumn(
    tracks: List<Song>,
    onSearchSongClickDebounce: (Song) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(top = 16.dp)
    ) {
        items(tracks) { track ->
            TrackItem(track, Modifier.clickable {
                onSearchSongClickDebounce(track)
            })
        }
    }
}

@Composable
fun TrackItem(
    song: Song,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {


        AsyncImage(
            model = song.artworkUrl100,
            contentDescription = null,
            modifier = Modifier
                .size(45.dp)
                .clip(RoundedCornerShape(2.dp)),
            contentScale = ContentScale.Crop,

            placeholder = painterResource(R.drawable.ic_track_placeholder),
            error = painterResource(R.drawable.ic_track_placeholder)
        )


        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp, vertical = 14.dp)
        ) {


            Text(
                text = song.trackName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall,
                color = colorResource(R.color.track_item_text_color)
            )


            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = song.artistName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                    color = colorResource(R.color.artist_name_and_time_color),
                    modifier = Modifier.widthIn(max = 200.dp),
                    fontSize = 11.sp
                )

                Icon(
                    painter = painterResource(R.drawable.dotsvg),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(horizontal = 5.dp, vertical = 6.dp)
                        .size(6.dp),
                    tint = colorResource(R.color.artist_name_and_time_color),
                )

                Text(
                    text = formatDuration(song.trackTimeMillis),
                    style = MaterialTheme.typography.bodySmall,
                    color = colorResource(R.color.artist_name_and_time_color),
                    modifier = modifier.wrapContentWidth(),
                    fontSize = 11.sp


                )
            }
        }


        Icon(
            painter = painterResource(R.drawable.ic_arrow_forward),
            contentDescription = null,
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 5.dp)
                .size(14.dp),
            tint = colorResource(R.color.artist_name_and_time_color),

            )
    }
}