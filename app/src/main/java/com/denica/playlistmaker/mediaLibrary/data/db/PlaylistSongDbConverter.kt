package com.denica.playlistmaker.mediaLibrary.data.db

import com.denica.playlistmaker.search.domain.models.Song

class PlaylistSongDbConverter {
    fun map(song: Song): PlaylistSongEntity {
        return PlaylistSongEntity(
            song.trackId,
            song.trackName,
            song.artistName,
            song.trackTimeMillis,
            song.artworkUrl100,
            song.collectionName,
            song.releaseDate ?: "",
            song.primaryGenreName,
            song.country,
            song.previewUrl,

            )
    }

    fun map(song: PlaylistSongEntity): Song {
        return Song(
            song.trackId,
            song.trackName,
            song.artistName,
            song.trackTimeMillis,
            song.artworkUrl100,
            song.collectionName,
            song.releaseDate,
            song.primaryGenreName,
            song.country,
            song.previewUrl,
        )
    }
}