package com.denica.playlistmaker.settings.domain.api

import com.denica.playlistmaker.search.data.Resource
import com.denica.playlistmaker.search.domain.models.Song
import com.denica.playlistmaker.settings.domain.model.ThemeSettings

interface SettingsRepository {
    fun saveSettings(isChecked: ThemeSettings)
    fun getSettings(): ThemeSettings
}