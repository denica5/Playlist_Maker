package com.denica.playlistmaker.search.data

import com.denica.playlistmaker.search.data.dto.Responce

interface NetworkClient {
    fun doRequest(dto: Any): Responce
}