package com.denica.playlistmaker.mediaLibrary.data.db

import com.denica.playlistmaker.search.domain.models.Song
import org.junit.Assert.assertEquals
import org.junit.Test

class SongDbConverterTest {

    private val converter = SongDbConverter()

    @Test
    fun `maps Song to SongEntity`() {
        val song = Song(
            trackId = 42L,
            trackName = "track",
            artistName = "artist",
            trackTimeMillis = 2000L,
            artworkUrl100 = "art",
            collectionName = "collection",
            releaseDate = null,
            primaryGenreName = "genre",
            country = "US",
            previewUrl = "url",
            isFavourite = false
        )

        val entity = converter.map(song)
        assertEquals(42L, entity.trackId)
        assertEquals("track", entity.trackName)
        assertEquals("artist", entity.artistName)
        assertEquals(2000L, entity.trackTimeMillis)
        assertEquals("art", entity.artworkUrl100)
        assertEquals("collection", entity.collectionName)
        assertEquals("", entity.releaseDate)
        assertEquals("genre", entity.primaryGenreName)
        assertEquals("US", entity.country)
        assertEquals("url", entity.previewUrl)
    }

    @Test
    fun `maps SongEntity to Song with isFavourite`() {
        val entity = SongEntity(
            trackId = 1L,
            trackName = "t",
            artistName = "a",
            trackTimeMillis = 1L,
            artworkUrl100 = "art",
            collectionName = "c",
            releaseDate = "2020",
            primaryGenreName = "g",
            country = "RU",
            previewUrl = "url",
            createAt = 1L
        )

        val song = converter.map(entity, isFavourite = true)
        assertEquals(
            Song(
                trackId = 1L,
                trackName = "t",
                artistName = "a",
                trackTimeMillis = 1L,
                artworkUrl100 = "art",
                collectionName = "c",
                releaseDate = "2020",
                primaryGenreName = "g",
                country = "RU",
                previewUrl = "url",
                isFavourite = true
            ),
            song
        )
    }
}

