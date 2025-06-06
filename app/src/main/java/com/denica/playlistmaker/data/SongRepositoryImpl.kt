package com.denica.playlistmaker.data

import com.denica.playlistmaker.data.dto.SongDto
import com.denica.playlistmaker.data.dto.SongRequest
import com.denica.playlistmaker.data.dto.SongResponse
import com.denica.playlistmaker.domain.api.SongRepository
import com.denica.playlistmaker.domain.models.Song

class SongRepositoryImpl(private val networkClient: NetworkClient) : SongRepository {
    override fun searchSong(expression: String): Pair<List<Song>, Boolean> {
        val response = networkClient.doRequest(SongRequest(expression))
        if (response.resultCode == 200) {
            return Pair((response as SongResponse).results.map {
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
            }, true)
        } else {
            return Pair(emptyList(), false)
        }
    }
}