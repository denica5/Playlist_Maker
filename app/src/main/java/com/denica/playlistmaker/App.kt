package com.denica.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.denica.playlistmaker.creator.Creator
import com.denica.playlistmaker.settings.domain.api.SettingsInteractor
import com.denica.playlistmaker.settings.domain.model.ThemeSettings
import com.denica.playlistmaker.settings.ui.DARK_THEME_MODE_KEY
import com.denica.playlistmaker.settings.ui.PLAYLIST_MAKER_PREFERENCES


class App : Application() {

    var darkTheme = false
    override fun onCreate() {
        super.onCreate()


        Creator.provideSettingsInteractor(this)
            .getSettings(object :
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