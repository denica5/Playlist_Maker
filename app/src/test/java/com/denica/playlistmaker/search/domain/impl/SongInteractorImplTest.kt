package com.denica.playlistmaker.search.domain.impl

import com.denica.playlistmaker.search.data.network.Resource
import com.denica.playlistmaker.search.domain.api.SongRepository
import com.denica.playlistmaker.search.domain.models.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SongInteractorImplTest {

    @Test
    fun `searchSong maps Success to data and null error`() = runBlocking {
        val songs = listOf(
            Song(
                trackId = 1L,
                trackName = "Track",
                artistName = "Artist",
                trackTimeMillis = 1000L,
                artworkUrl100 = "art",
                collectionName = "collection",
                releaseDate = "2020-01-01",
                primaryGenreName = "genre",
                country = "US",
                previewUrl = "url",
                isFavourite = false
            )
        )

        val repository = FakeSongRepository(flowOf(Resource.Success(songs)))
        val interactor = SongInteractorImpl(repository)

        val result = interactor.searchSong("q").first()

        assertEquals(songs, result.first)
        assertNull(result.second)
    }

    @Test
    fun `searchSong maps Error to null data and message`() = runBlocking {
        val repository = FakeSongRepository(flowOf(Resource.Error("boom")))
        val interactor = SongInteractorImpl(repository)

        val result = interactor.searchSong("q").first()

        assertNull(result.first)
        assertEquals("boom", result.second)
    }

    private class FakeSongRepository(
        private val result: Flow<Resource<List<Song>>>
    ) : SongRepository {
        override fun searchSong(expression: String): Flow<Resource<List<Song>>> = result
    }
}

