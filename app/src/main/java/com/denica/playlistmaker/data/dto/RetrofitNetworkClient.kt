package com.denica.playlistmaker.data.dto

import com.denica.playlistmaker.data.NetworkClient
import com.denica.playlistmaker.data.network.ItunesApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


const val ITUNES_BASE_URL = "https://itunes.apple.com"

class RetrofitNetworkClient : NetworkClient {

    private val retrofit = Retrofit.Builder()
        .baseUrl(ITUNES_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(ItunesApi::class.java)

    override fun doRequest(dto: Any): Responce {
        if (dto is SongRequest) {
            val resp = itunesService.search(dto.expression)
                .execute()

            val body = resp.body() ?: Responce()

            return body.apply { resultCode = resp.code() }
        } else {
            return Responce()
                .apply { resultCode = 400 }
        }
    }
}
