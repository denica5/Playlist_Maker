package com.denica.playlistmaker.search.data.network

import com.denica.playlistmaker.search.data.dto.Responce

interface NetworkClient {
    suspend fun doRequest(dto: Any): Responce
}