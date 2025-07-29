package com.denica.playlistmaker.search.data.dto

class SongResponse(
    val resultCount: Int,
    val expression: String,
    val results: List<SongDto>
) : Responce()

