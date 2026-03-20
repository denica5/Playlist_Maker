package com.denica.playlistmaker.mediaLibrary.data.db

import com.denica.playlistmaker.mediaLibrary.domain.Playlist
import org.junit.Assert.assertEquals
import org.junit.Test

class PlaylistDbConverterTest {

    private val converter = PlaylistDbConverter()

    @Test
    fun `maps PlaylistEntity to Playlist and back`() {
        val entity = PlaylistEntity(
            playlistId = 7L,
            name = "name",
            description = "desc",
            imagePath = "/img",
            trackIds = listOf(1L, 2L, 3L),
            trackCount = 3
        )

        val playlist = converter.map(entity)
        assertEquals(
            Playlist(
                id = 7L,
                name = "name",
                description = "desc",
                imagePath = "/img",
                trackIds = listOf(1L, 2L, 3L),
                trackCount = 3
            ),
            playlist
        )

        val mappedBack = converter.map(playlist)
        assertEquals(entity, mappedBack)
    }
}

