package com.denica.playlistmaker.di

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.denica.playlistmaker.mediaLibrary.data.db.AppDatabase
import com.denica.playlistmaker.mediaLibrary.domain.DbPlaylistInteractor
import com.denica.playlistmaker.mediaLibrary.domain.DbSongInteractor
import com.denica.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.denica.playlistmaker.search.domain.api.SongInteractor
import com.denica.playlistmaker.settings.domain.api.SettingsInteractor
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.koinApplication

@RunWith(AndroidJUnit4::class)
class KoinModulesAndroidTest {

    @Test
    fun koinModules_resolveCoreDependencies() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext


        val koinApp = koinApplication {
            androidContext(context)
            modules(dataModule, repositoryModule, interactorModule, viewModelModule)
        }

        val koin = koinApp.koin

        koin.get<AppDatabase>()
        koin.get<SongInteractor>()
        koin.get<SearchHistoryInteractor>()
        koin.get<SettingsInteractor>()
        koin.get<DbSongInteractor>()
        koin.get<DbPlaylistInteractor>()
    }
}
