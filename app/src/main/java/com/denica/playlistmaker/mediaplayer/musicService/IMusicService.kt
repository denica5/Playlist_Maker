package com.denica.playlistmaker.mediaplayer.musicService

import com.denica.playlistmaker.mediaplayer.ui.PlayerState
import kotlinx.coroutines.flow.StateFlow

interface IMusicService {
    val playerState: StateFlow<PlayerState>
    fun pausePlayer()
    fun startPlayer()
    fun showNotification()
    fun hideNotification()
}