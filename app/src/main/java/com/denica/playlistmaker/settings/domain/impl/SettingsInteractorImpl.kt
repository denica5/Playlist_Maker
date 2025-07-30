package com.denica.playlistmaker.settings.domain.impl

import com.denica.playlistmaker.settings.domain.api.SettingsInteractor
import com.denica.playlistmaker.settings.domain.api.SettingsRepository
import com.denica.playlistmaker.settings.domain.model.ThemeSettings

class SettingsInteractorImpl(private val repository: SettingsRepository) : SettingsInteractor {
    override fun saveSettings(isChecked: ThemeSettings) {
        repository.saveSettings(isChecked)
    }

    override fun getSettings(consumer: SettingsInteractor.SettingsConsumer) {
        consumer.consume(repository.getSettings())
    }
}