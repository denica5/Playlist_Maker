package com.denica.playlistmaker.search.data.network

import com.denica.playlistmaker.search.data.dto.SongRequest
import com.denica.playlistmaker.search.data.dto.SongResponse
import com.denica.playlistmaker.search.domain.api.SongRepository
import com.denica.playlistmaker.search.domain.models.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SongRepositoryImpl(private val networkClient: NetworkClient) : SongRepository {
    override fun searchSong(expression: String): Flow<Resource<List<Song>>> = flow {
        val response = networkClient.doRequest(SongRequest(expression))

        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error("Проверьте подключение к интернету"))
            }

            200 -> {
                with(response as SongResponse) {
                    val data = response.results.map {
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
                    }

                    emit(Resource.Success(data))
                }
            }

            else -> {
                emit(Resource.Error("Ошибка сервера"))
            }
        }
    }
}