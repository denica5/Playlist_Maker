package com.denica.playlistmaker.search.data.network

import com.denica.playlistmaker.search.data.dto.Responce
import com.denica.playlistmaker.search.data.dto.SongRequest


const val ITUNES_BASE_URL = "https://itunes.apple.com"

class RetrofitNetworkClient(private val itunesService: ItunesApi) : NetworkClient {



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
