package com.denica.playlistmaker.settings.domain.impl

import com.denica.playlistmaker.settings.domain.api.SettingsRepository
import com.denica.playlistmaker.settings.domain.model.ThemeSettings
import org.junit.Assert.assertEquals
import org.junit.Test

class SettingsInteractorImplTest {

    @Test
    fun `saveSettings delegates to repository`() {
        val repository = FakeSettingsRepository()
        val interactor = SettingsInteractorImpl(repository)

        interactor.saveSettings(ThemeSettings(isChecked = true))

        assertEquals(ThemeSettings(true), repository.saved)
    }

    @Test
    fun `getSettings returns repository value to consumer`() {
        val repository = FakeSettingsRepository(current = ThemeSettings(isChecked = false))
        val interactor = SettingsInteractorImpl(repository)

        var consumed: ThemeSettings? = null
        interactor.getSettings { theme -> consumed = theme }

        assertEquals(ThemeSettings(false), consumed)
    }

    private class FakeSettingsRepository(
        private var current: ThemeSettings = ThemeSettings(isChecked = true)
    ) : SettingsRepository {
        var saved: ThemeSettings? = null

        override fun saveSettings(isChecked: ThemeSettings) {
            saved = isChecked
            current = isChecked
        }

        override fun getSettings(): ThemeSettings = current
    }

    private fun SettingsInteractorImpl.getSettings(consumer: (ThemeSettings) -> Unit) {
        getSettings(object : com.denica.playlistmaker.settings.domain.api.SettingsInteractor.SettingsConsumer {
            override fun consume(isChecked: ThemeSettings) {
                consumer(isChecked)
            }
        })
    }
}

