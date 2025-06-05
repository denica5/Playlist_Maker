package com.denica.playlistmaker.ui.search

import com.denica.playlistmaker.data.dto.SongDto
import com.denica.playlistmaker.domain.models.Song

interface OnItemClickListener {
    fun onItemClick(song: Song)
}