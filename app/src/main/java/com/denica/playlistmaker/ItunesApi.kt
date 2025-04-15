package com.denica.playlistmaker

import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesApi {
    @GET("/search?entity=song")
    fun search(@Query("term") text: String)
}

class SongResponse(
    val resultCount: Int,
    val results: List<Song>
)

class Song(
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String
)