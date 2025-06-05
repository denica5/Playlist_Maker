package com.denica.playlistmaker.data

import com.denica.playlistmaker.data.dto.Responce
interface NetworkClient {
    fun doRequest(dto: Any): Responce
}