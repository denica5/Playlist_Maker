package com.denica.playlistmaker.mediaLibrary.data.db

import com.denica.playlistmaker.mediaLibrary.domain.Playlist

class PlaylistDbConverter {
    fun map(playlist: PlaylistEntity): Playlist {
        return Playlist(
            playlist.playlistId,
            playlist.name,
            playlist.description,
            playlist.imagePath,
            playlist.trackIds,
            playlist.trackCount

        )
    }

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlist.id,
            playlist.name,
            playlist.description,
            playlist.imagePath,
            playlist.trackIds,
            playlist.trackCount
        )
    }
}