package com.denica.playlistmaker.settings.ui

import android.content.Intent
import androidx.appcompat.widget.DialogTitle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.denica.playlistmaker.settings.domain.api.SettingsInteractor
import com.denica.playlistmaker.settings.domain.api.SharingInteractor
import com.denica.playlistmaker.settings.domain.model.ThemeSettings
import org.w3c.dom.Text

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val shareInteractor: SharingInteractor
) : ViewModel() {

    private var isDarkTheme: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    fun saveSettings(isChecked: Boolean) {
        val themeSettings = ThemeSettings(isChecked)
        settingsInteractor.saveSettings(themeSettings)
    }

    private fun getStartSettings() {
        settingsInteractor.getSettings(object : SettingsInteractor.SettingsConsumer {
            override fun consume(isChecked: ThemeSettings) {
                isDarkTheme.postValue(isChecked.isChecked)
            }

        })
    }

    fun shareApp(): Intent = shareInteractor.shareApp()
    fun supportApp(): Intent = shareInteractor.openSupport()
    fun termsApp(): Intent = shareInteractor.openTerms()


    init {
        getStartSettings()
    }



}