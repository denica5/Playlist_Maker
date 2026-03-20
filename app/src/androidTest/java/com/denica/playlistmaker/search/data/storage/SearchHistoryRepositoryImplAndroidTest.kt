package com.denica.playlistmaker.search.data.storage

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.denica.playlistmaker.mediaLibrary.data.db.AppDatabase
import com.denica.playlistmaker.mediaLibrary.data.db.SongEntity
import com.denica.playlistmaker.search.domain.models.Song
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchHistoryRepositoryImplAndroidTest {

    private lateinit var db: AppDatabase
    private lateinit var storage: FakeStorageClient
    private lateinit var repository: SearchHistoryRepositoryImpl

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        storage = FakeStorageClient()
        repository = SearchHistoryRepositoryImpl(storage, db)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun saveToHistory_movesExistingTrackToFront_andReturnsPreviousPosition() = runBlocking {
        for (i in 1..6) {
            repository.saveToHistory(song(trackId = i.toLong()))
        }

        val position = repository.saveToHistory(song(trackId = 5L))

        assertEquals(1, position)
        assertEquals(listOf(5L, 6L, 4L, 3L, 2L, 1L), storage.requireData().map { it.trackId })
    }

    @Test
    fun saveToHistory_keepsOnlyLast10Tracks() = runBlocking {
        for (i in 1..12) {
            repository.saveToHistory(song(trackId = i.toLong()))
        }

        val ids = storage.requireData().map { it.trackId }
        assertEquals(10, ids.size)
        assertEquals(12L, ids.first())
        assertTrue(1L !in ids)
        assertTrue(2L !in ids)
        assertTrue(3L in ids)
    }

    @Test
    fun getHistory_setsIsFavourite_basedOnDbIds() = runBlocking {
        db.songDao().addSongToFavourite(
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

        repository.saveToHistory(song(trackId = 1L))

        val saved = storage.requireData().single()
        assertTrue(saved.isFavourite)
    }

    private fun song(trackId: Long): Song =
        Song(
            trackId = trackId,
            trackName = "t$trackId",
            artistName = "a$trackId",
            trackTimeMillis = 1L,
            artworkUrl100 = "art",
            collectionName = "c",
            releaseDate = "2020",
            primaryGenreName = "g",
            country = "RU",
            previewUrl = "url"
        )

    private class FakeStorageClient : StorageClient<ArrayList<Song>> {
        private var data: ArrayList<Song>? = null

        override fun storeData(data: ArrayList<Song>) {
            // Store a deep-ish copy so tests can safely inspect without being affected by later mutations.
            this.data = ArrayList(data.map { it.copy(isFavourite = it.isFavourite) })
        }

        override fun getData(): ArrayList<Song>? = data?.let { ArrayList(it.map { s -> s.copy(isFavourite = s.isFavourite) }) }

        fun requireData(): ArrayList<Song> = checkNotNull(getData()) { "Expected history to be saved, but it was null" }
    }
}

