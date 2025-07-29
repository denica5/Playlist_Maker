package com.denica.playlistmaker.settings.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.denica.playlistmaker.creator.Creator
import com.denica.playlistmaker.settings.domain.api.SettingsInteractor
import com.denica.playlistmaker.settings.domain.api.SharingInteractor
import com.denica.playlistmaker.settings.domain.model.ThemeSettings

class SettingsViewModel(
    val context: Context
) : ViewModel() {
    private val settingsInteractor: SettingsInteractor = Creator.provideSettingsInteractor(context)
    private val shareInteractor: SharingInteractor = Creator.provideShareInteractor(context)
    private var isDarkTheme: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    fun isDarkTheme(): LiveData<Boolean> = isDarkTheme
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

    companion object {
        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as Application)
                SettingsViewModel(
                    app
                )
            }
        }
    }
}