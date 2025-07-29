package com.denica.playlistmaker.search.data

import com.denica.playlistmaker.search.data.dto.SongRequest
import com.denica.playlistmaker.search.data.dto.SongResponse
import com.denica.playlistmaker.search.domain.api.SongRepository
import com.denica.playlistmaker.search.domain.models.Song

class SongRepositoryImpl(private val networkClient: NetworkClient) : SongRepository {
    override fun searchSong(expression: String): Resource<List<Song>> {
        val response = networkClient.doRequest(SongRequest(expression))

        return when (response.resultCode) {
            -1 -> {
                Resource.Error("Проверьте подключение к интернету")
            }

            200 -> {
                Resource.Success((response as SongResponse).results.map {
                    Song(
                        it.trackId ?: 0,
                        it.trackName ?: "",
                        it.artistName ?: "",
                        it.trackTimeMillis ?: 0L,
                        it.artworkUrl100 ?: "",
                        it.collectionName ?: "",
                        it.releaseDate ?: "",
                        it.primaryGenreName ?: "",
                        it.country ?: "",
                        it.previewUrl ?: ""
                    )
                })
            }

            else -> {
                Resource.Error("Ошибка сервера")
            }
        }
    }
}