package com.denica.playlistmaker.mediaLibrary.data.db

import com.denica.playlistmaker.search.domain.models.Song

class SongDbConverter {
    fun map(song: Song): SongEntity {
        return SongEntity(
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

    fun map(song: SongEntity, isFavourite: Boolean): Song {
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
            isFavourite
        )
    }
}