package com.denica.playlistmaker.settings.data

import com.denica.playlistmaker.settings.domain.api.SettingsRepository

class SettingsRepositoryImpl(private val storage: StorageClient<Boolean>) : SettingsRepository {
    override fun saveSettings(isChecked: Boolean) {
        storage.storeData(isChecked)
    }

    override fun getSettings(): Boolean {
        return storage.getData() ?: false
    }
}