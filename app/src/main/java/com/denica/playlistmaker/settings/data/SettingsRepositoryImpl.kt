package com.denica.playlistmaker.settings.data

import com.denica.playlistmaker.settings.domain.api.SettingsRepository
import com.denica.playlistmaker.settings.domain.model.ThemeSettings

class SettingsRepositoryImpl(private val storage: StorageClient<ThemeSettings>) : SettingsRepository {
    override fun saveSettings(isChecked: ThemeSettings) {
        storage.storeData(isChecked)
    }

    override fun getSettings(): ThemeSettings {
        return storage.getData() ?: ThemeSettings(false)
    }
}