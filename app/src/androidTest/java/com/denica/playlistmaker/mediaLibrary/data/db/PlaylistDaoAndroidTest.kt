package com.denica.playlistmaker.mediaLibrary.data.db

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PlaylistDaoAndroidTest {

    private lateinit var db: AppDatabase
    private lateinit var playlistDao: PlaylistDao

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        playlistDao = db.playlistDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun addTrackToPlayList_returnsMinus1_whenPlaylistNotFound() = runBlocking {
        val result = playlistDao.addTrackToPlayList(playlistId = 999L, trackId = 10L)
        assertEquals(-1, result)
    }

    @Test
    fun addTrackToPlayList_addsTrack_updatesCount_andAvoidsDuplicates() = runBlocking {
        playlistDao.insertPlaylist(
            PlaylistEntity(
                name = "name",
                description = "desc",
                imagePath = "",
                trackIds = emptyList(),
                trackCount = 0
            )
        )
        val playlistId = playlistDao.getPlaylistList().first().firstOrNull()?.playlistId
        assertNotNull(playlistId)

        val firstAdd = playlistDao.addTrackToPlayList(playlistId = playlistId!!, trackId = 10L)
        val secondAdd = playlistDao.addTrackToPlayList(playlistId = playlistId, trackId = 10L)

        assertEquals(1, firstAdd)
        assertEquals(0, secondAdd)

        val updated = playlistDao.getPlaylist(playlistId)
        assertNotNull(updated)
        assertEquals(listOf(10L), updated!!.trackIds)
        assertEquals(1, updated.trackCount)
    }

    @Test
    fun removeTrackToPlayList_removesTrack_andUpdatesCount() = runBlocking {
        playlistDao.insertPlaylist(
            PlaylistEntity(
                name = "name",
                description = "desc",
                imagePath = "",
                trackIds = listOf(10L),
                trackCount = 1
            )
        )
        val playlistId = playlistDao.getPlaylistList().first().firstOrNull()?.playlistId
        assertNotNull(playlistId)

        playlistDao.removeTrackToPlayList(playlistId = playlistId!!, trackId = 10L)

        val updated = playlistDao.getPlaylist(playlistId)
        assertNotNull(updated)
        assertEquals(emptyList<Long>(), updated!!.trackIds)
        assertEquals(0, updated.trackCount)
    }
}

