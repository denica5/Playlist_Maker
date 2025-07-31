package com.denica.playlistmaker.search.data.network

import com.denica.playlistmaker.search.data.NetworkClient
import com.denica.playlistmaker.search.data.dto.Responce
import com.denica.playlistmaker.search.data.dto.SongRequest
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
            val resp = try{
                itunesService.search(dto.expression)
                    .execute()
            } catch(e: Exception) {
                null
            }

            val body = resp?.body() ?: Responce()

            return body.apply { resultCode = resp?.code() ?: -1 }
        } else {
            return Responce()
                .apply { resultCode = 400 }
        }
    }
}
