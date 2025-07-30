package com.denica.playlistmaker.settings.domain.api

import com.denica.playlistmaker.search.data.Resource
import com.denica.playlistmaker.search.domain.models.Song

interface SettingsRepository {
    fun saveSettings(isChecked: Boolean)
    fun getSettings(): Boolean
}