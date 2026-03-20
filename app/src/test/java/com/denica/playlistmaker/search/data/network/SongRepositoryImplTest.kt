package com.denica.playlistmaker.search.data.network

import com.denica.playlistmaker.search.data.dto.Responce
import com.denica.playlistmaker.search.data.dto.SongDto
import com.denica.playlistmaker.search.data.dto.SongResponse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SongRepositoryImplTest {

    @Test
    fun `searchSong emits internet error when resultCode is -1`() = runBlocking {
        val repository = SongRepositoryImpl(FakeNetworkClient(Responce().apply { resultCode = -1 }))

        val result = repository.searchSong("q").first()

        assertTrue(result is Resource.Error)
        assertEquals("Проверьте подключение к интернету", result.message)
    }

    @Test
    fun `searchSong emits server error when resultCode is not -1 or 200`() = runBlocking {
        val repository = SongRepositoryImpl(FakeNetworkClient(Responce().apply { resultCode = 500 }))

        val result = repository.searchSong("q").first()

        assertTrue(result is Resource.Error)
        assertEquals("Ошибка сервера", result.message)
    }

    @Test
    fun `searchSong maps nullable dto fields to defaults`() = runBlocking {
        val response = SongResponse(
            resultCount = 1,
            expression = "q",
            results = listOf(
                SongDto(
                    trackId = null,
                    trackName = null,
                    artistName = null,
                    trackTimeMillis = null,
                    artworkUrl100 = null,
                    collectionName = null,
                    releaseDate = null,
                    primaryGenreName = null,
                    country = null,
                    previewUrl = null
                )
            )
        ).apply { resultCode = 200 }

        val repository = SongRepositoryImpl(FakeNetworkClient(response))

        val result = repository.searchSong("q").first()

        assertTrue(result is Resource.Success)
        val song = (result as Resource.Success).data!!.single()
        assertEquals(0L, song.trackId)
        assertEquals("", song.trackName)
        assertEquals("", song.artistName)
        assertEquals(0L, song.trackTimeMillis)
        assertEquals("", song.artworkUrl100)
        assertEquals("", song.collectionName)
        assertEquals("", song.releaseDate)
        assertEquals("", song.primaryGenreName)
        assertEquals("", song.country)
        assertEquals("", song.previewUrl)
    }

    private class FakeNetworkClient(
        private val response: Responce
    ) : NetworkClient {
        override suspend fun doRequest(dto: Any): Responce = response
    }
}

