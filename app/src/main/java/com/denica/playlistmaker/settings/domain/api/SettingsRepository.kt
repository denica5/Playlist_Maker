package com.denica.playlistmaker.settings.domain.api

import com.denica.playlistmaker.settings.domain.model.ThemeSettings

interface SettingsRepository {
    fun saveSettings(isChecked: ThemeSettings)
    fun getSettings(): ThemeSettings
}