package com.denica.playlistmaker.search.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.denica.playlistmaker.search.data.dto.Responce
import com.denica.playlistmaker.search.data.dto.SongRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


const val ITUNES_BASE_URL = "https://itunes.apple.com"

class RetrofitNetworkClient(private val itunesService: ItunesApi, private val context: Context) :
    NetworkClient {


    override suspend fun doRequest(dto: Any): Responce {
        if (!isConnected()) {
            return Responce().apply { resultCode = -1 }
        }
        if (dto !is SongRequest) {
            return Responce().apply { resultCode = 400 }
        }
        return withContext(Dispatchers.IO) {
            try {
                val response = itunesService.search(dto.expression)
                response.apply { resultCode = 200 }
            } catch (e: Throwable) {
                Responce().apply { resultCode = 500 }
            }
        }
    }

    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }
}
