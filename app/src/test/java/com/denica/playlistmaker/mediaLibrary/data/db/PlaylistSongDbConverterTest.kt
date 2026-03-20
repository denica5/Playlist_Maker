package com.denica.playlistmaker.mediaLibrary.data.db

import com.denica.playlistmaker.search.domain.models.Song
import org.junit.Assert.assertEquals
import org.junit.Test

class PlaylistSongDbConverterTest {

    private val converter = PlaylistSongDbConverter()

    @Test
    fun `maps Song to PlaylistSongEntity and back`() {
        val song = Song(
            trackId = 10L,
            trackName = "t",
            artistName = "a",
            trackTimeMillis = 123L,
            artworkUrl100 = "art",
            collectionName = "c",
            releaseDate = "2021-01-01",
            primaryGenreName = "g",
            country = "RU",
            previewUrl = "url",
            isFavourite = true
        )

        val entity = converter.map(song)
        assertEquals(10L, entity.trackId)
        assertEquals("t", entity.trackName)
        assertEquals("a", entity.artistName)
        assertEquals(123L, entity.trackTimeMillis)
        assertEquals("art", entity.artworkUrl100)
        assertEquals("c", entity.collectionName)
        assertEquals("2021-01-01", entity.releaseDate)
        assertEquals("g", entity.primaryGenreName)
        assertEquals("RU", entity.country)
        assertEquals("url", entity.previewUrl)

        val mappedBack = converter.map(entity)
        assertEquals(song.copy(isFavourite = false), mappedBack)
    }
}

