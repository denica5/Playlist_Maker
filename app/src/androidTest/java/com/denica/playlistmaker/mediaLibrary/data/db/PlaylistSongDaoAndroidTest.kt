package com.denica.playlistmaker.mediaLibrary.data.db

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PlaylistSongDaoAndroidTest {

    private lateinit var db: AppDatabase
    private lateinit var playlistSongDao: PlaylistSongDao

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        playlistSongDao = db.playlistSongDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun addPlaylistSong_ignoresDuplicates_andOrdersByCreateAtDesc() = runBlocking {
        playlistSongDao.addPlaylistSong(
            PlaylistSongEntity(
                trackId = 1L,
                trackName = "t1",
                artistName = "a1",
                trackTimeMillis = 1L,
                artworkUrl100 = "art",
                collectionName = "c",
                releaseDate = "2020",
                primaryGenreName = "g",
                country = "RU",
                previewUrl = "url",
                createAt = 100L
            )
        )
        playlistSongDao.addPlaylistSong(
            PlaylistSongEntity(
                trackId = 2L,
                trackName = "t2",
                artistName = "a2",
                trackTimeMillis = 2L,
                artworkUrl100 = "art",
                collectionName = "c",
                releaseDate = "2020",
                primaryGenreName = "g",
                country = "RU",
                previewUrl = "url",
                createAt = 200L
            )
        )
        playlistSongDao.addPlaylistSong(
            PlaylistSongEntity(
                trackId = 3L,
                trackName = "t3",
                artistName = "a3",
                trackTimeMillis = 3L,
                artworkUrl100 = "art",
                collectionName = "c",
                releaseDate = "2020",
                primaryGenreName = "g",
                country = "RU",
                previewUrl = "url",
                createAt = 150L
            )
        )

        // Duplicate insert should be ignored
        playlistSongDao.addPlaylistSong(
            PlaylistSongEntity(
                trackId = 2L,
                trackName = "t2-dup",
                artistName = "a2-dup",
                trackTimeMillis = 999L,
                artworkUrl100 = "art",
                collectionName = "c",
                releaseDate = "2020",
                primaryGenreName = "g",
                country = "RU",
                previewUrl = "url",
                createAt = 999L
            )
        )

        val songs = playlistSongDao.getPlaylistSongsByIds(listOf(1L, 2L, 3L)).first()

        assertEquals(listOf(2L, 3L, 1L), songs.map { it.trackId })
    }
}

