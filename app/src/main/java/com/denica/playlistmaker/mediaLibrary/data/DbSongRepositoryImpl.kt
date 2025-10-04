package com.denica.playlistmaker.mediaLibrary.data

import com.denica.playlistmaker.mediaLibrary.data.db.SongDao
import com.denica.playlistmaker.mediaLibrary.data.db.SongDbConverter
import com.denica.playlistmaker.mediaLibrary.data.db.SongEntity
import com.denica.playlistmaker.mediaLibrary.domain.DbSongRepository
import com.denica.playlistmaker.search.domain.models.Song
import kotlinx.coroutines.flow.map

class DbSongRepositoryImpl(
    private val songDao: SongDao,
    private val songDbConverter: SongDbConverter

) : DbSongRepository {
    override suspend fun addSongToFavourite(song: Song) {
        songDao.addSongToFavourite(songDbConverter.map(song))
    }

    override suspend fun deleteSongFromFavourite(song: Song) {
        songDao.deleteSongFromFavourite(songDbConverter.map(song))
    }

    override  fun getFavouriteSongs() = songDao.getFavouriteSongs().map { convertFromFavouriteSongEntity(it) }


    override suspend fun getFavouriteSongsIds(): List<Long> {
        return songDao.getFavouriteSongsIds()
    }

    private fun convertFromFavouriteSongEntity(songs: List<SongEntity>): List<Song> {
        return songs.map {
            songDbConverter.map(it, true)
        }
    }
}