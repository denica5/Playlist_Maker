package com.denica.playlistmaker.mediaLibrary.data

import com.denica.playlistmaker.mediaLibrary.data.db.SongDao
import com.denica.playlistmaker.mediaLibrary.data.db.SongDbConverter
import com.denica.playlistmaker.mediaLibrary.data.db.SongEntity
import com.denica.playlistmaker.search.domain.models.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DbSongRepositoryImplTest {

    @Test
    fun `addSongToFavourite delegates to dao with mapped entity`() = runBlocking {
        val dao = FakeSongDao()
        val repository = DbSongRepositoryImpl(dao, SongDbConverter())

        val song = Song(
            trackId = 5L,
            trackName = "t",
            artistName = "a",
            trackTimeMillis = 10L,
            artworkUrl100 = "art",
            collectionName = "c",
            releaseDate = null,
            primaryGenreName = "g",
            country = "RU",
            previewUrl = "url"
        )

        repository.addSongToFavourite(song)

        assertEquals(1, dao.added.size)
        assertEquals(5L, dao.added.single().trackId)
    }

    @Test
    fun `deleteSongFromFavourite delegates to dao with mapped entity`() = runBlocking {
        val dao = FakeSongDao()
        val repository = DbSongRepositoryImpl(dao, SongDbConverter())

        val song = Song(
            trackId = 6L,
            trackName = "t",
            artistName = "a",
            trackTimeMillis = 10L,
            artworkUrl100 = "art",
            collectionName = "c",
            releaseDate = "2020",
            primaryGenreName = "g",
            country = "RU",
            previewUrl = "url"
        )

        repository.deleteSongFromFavourite(song)

        assertEquals(1, dao.deleted.size)
        assertEquals(6L, dao.deleted.single().trackId)
    }

    @Test
    fun `getFavouriteSongs maps entities with isFavourite true`() = runBlocking {
        val dao = FakeSongDao()
        dao.favouriteFlow.value = listOf(
            SongEntity(
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
        )
        val repository = DbSongRepositoryImpl(dao, SongDbConverter())

        val songs = repository.getFavouriteSongs().first()

        assertEquals(1, songs.size)
        assertTrue(songs.single().isFavourite)
    }

    private class FakeSongDao : SongDao {
        val added = mutableListOf<SongEntity>()
        val deleted = mutableListOf<SongEntity>()
        val favouriteFlow = MutableStateFlow<List<SongEntity>>(emptyList())
        var favouriteIds: List<Long> = emptyList()

        override suspend fun addSongToFavourite(song: SongEntity) {
            added += song
            favouriteIds = (favouriteIds + song.trackId).distinct()
        }

        override suspend fun deleteSongFromFavourite(song: SongEntity) {
            deleted += song
            favouriteIds = favouriteIds.filterNot { it == song.trackId }
        }

        override fun getFavouriteSongs(): Flow<List<SongEntity>> = favouriteFlow

        override suspend fun getFavouriteSongsIds(): List<Long> = favouriteIds
    }
}

