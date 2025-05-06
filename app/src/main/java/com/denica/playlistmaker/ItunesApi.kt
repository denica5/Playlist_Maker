package com.denica.playlistmaker

import android.os.Parcelable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.Serializable

interface ItunesApi {
    @GET("/search?entity=song")
    fun search(@Query("term") text: String) : Call<SongResponse>
}

class SongResponse(
    val resultCount: Int,
    val results: List<Track>
)

class Track(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
) :Serializable