package com.denica.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.denica.playlistmaker.di.dataModule
import com.denica.playlistmaker.di.interactorModule
import com.denica.playlistmaker.di.repositoryModule
import com.denica.playlistmaker.di.viewModelModule
import com.denica.playlistmaker.settings.domain.api.SettingsInteractor
import com.denica.playlistmaker.settings.domain.model.ThemeSettings
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class App : Application() {

    var darkTheme = false
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(dataModule, repositoryModule, interactorModule, viewModelModule)

        }

        val interactor: SettingsInteractor by inject()
        interactor.getSettings(object :
            SettingsInteractor.SettingsConsumer {
            override fun consume(isChecked: ThemeSettings) {
                darkTheme = isChecked.isChecked
            }
        }
        )
        switchTheme(darkTheme)
    }


    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}