package com.denica.playlistmaker.settings.domain.api

import com.denica.playlistmaker.settings.domain.model.ThemeSettings

interface SettingsInteractor {
    fun saveSettings(isChecked: ThemeSettings)
    fun getSettings(consumer: SettingsConsumer)
    interface SettingsConsumer {
        fun consume(isChecked: ThemeSettings)
    }
}