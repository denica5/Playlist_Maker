package com.denica.playlistmaker.search.data.network

import com.denica.playlistmaker.search.data.dto.SongResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesApi {
    @GET("/search?entity=song")
    fun search(@Query("term") text: String): Call<SongResponse>
}