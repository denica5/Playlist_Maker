package com.denica.playlistmaker.settings.domain

import com.denica.playlistmaker.settings.domain.api.SettingsInteractor
import com.denica.playlistmaker.settings.domain.api.SettingsRepository

class SettingsInteractorImpl(val repository: SettingsRepository) : SettingsInteractor {
    override fun saveSettings(isChecked: Boolean) {
        repository.saveSettings(isChecked)
    }

    override fun getSettings(consumer: SettingsInteractor.SettingsConsumer) {
        consumer.consume(repository.getSettings())
    }
}