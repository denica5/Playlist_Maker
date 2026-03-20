package com.denica.playlistmaker.mediaLibrary.data

import com.denica.playlistmaker.mediaLibrary.data.db.PlaylistDao
import com.denica.playlistmaker.mediaLibrary.data.db.PlaylistDbConverter
import com.denica.playlistmaker.mediaLibrary.data.db.PlaylistEntity
import com.denica.playlistmaker.mediaLibrary.data.db.PlaylistSongDao
import com.denica.playlistmaker.mediaLibrary.data.db.PlaylistSongDbConverter
import com.denica.playlistmaker.mediaLibrary.data.db.PlaylistSongEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class DbPlaylistRepositoryImplTest {

    @Test
    fun `getPlaylist returns id -1 when dao returns null`() = runBlocking {
        val repository = DbPlaylistRepositoryImpl(
            playlistDao = FakePlaylistDao(playlistToReturn = null),
            playlistSongDao = FakePlaylistSongDao(),
            playlistDbConverter = PlaylistDbConverter(),
            playlistSongDbConverter = PlaylistSongDbConverter()
        )

        val playlist = repository.getPlaylist(playlistId = 123L)

        assertEquals(-1L, playlist?.id)
    }

    @Test
    fun `removeTrackIfLastInPlaylists deletes only when track not present`() = runBlocking {
        val playlistSongDao = FakePlaylistSongDao()
        val repository = DbPlaylistRepositoryImpl(
            playlistDao = FakePlaylistDao(playlistToReturn = null),
            playlistSongDao = playlistSongDao,
            playlistDbConverter = PlaylistDbConverter(),
            playlistSongDbConverter = PlaylistSongDbConverter()
        )

        repository.removeTrackIfLastInPlaylists(trackId = 10L, tracksIds = listOf(1L, 2L, 3L))
        assertEquals(listOf(10L), playlistSongDao.deletedTrackIds)

        playlistSongDao.deletedTrackIds.clear()
        repository.removeTrackIfLastInPlaylists(trackId = 10L, tracksIds = listOf(10L, 2L, 3L))
        assertEquals(emptyList<Long>(), playlistSongDao.deletedTrackIds)
    }

    private class FakePlaylistSongDao : PlaylistSongDao {
        val deletedTrackIds = mutableListOf<Long>()

        override suspend fun addPlaylistSong(playlistSongEntity: PlaylistSongEntity) = Unit

        override fun getPlaylistSongsByIds(tracksIds: List<Long>): Flow<List<PlaylistSongEntity>> =
            flowOf(emptyList())

        override suspend fun deletePlaylistSong(trackId: Long) {
            deletedTrackIds += trackId
        }
    }

    private class FakePlaylistDao(
        private val playlistToReturn: PlaylistEntity?
    ) : PlaylistDao {
        override suspend fun insertPlaylist(playlist: PlaylistEntity) = Unit
        override suspend fun updatePlaylist(playlist: PlaylistEntity) = Unit
        override suspend fun deletePlaylist(playlistId: Long) = Unit
        override fun getPlaylistList(): Flow<List<PlaylistEntity>> = flowOf(emptyList())
        override suspend fun getPlaylist(playlistId: Long): PlaylistEntity? = playlistToReturn
        override suspend fun addTrackToPlayList(playlistId: Long, trackId: Long): Int = 0
        override suspend fun removeTrackToPlayList(playlistId: Long, trackId: Long) = Unit
    }
}

