package com.denica.playlistmaker.di

import com.denica.playlistmaker.search.data.network.SongRepositoryImpl
import com.denica.playlistmaker.search.data.storage.SearchHistoryRepositoryImpl
import com.denica.playlistmaker.search.domain.api.SearchHistoryRepository
import com.denica.playlistmaker.search.domain.api.SongRepository
import com.denica.playlistmaker.settings.data.SettingsRepositoryImpl
import com.denica.playlistmaker.settings.data.ShareRepositoryImpl
import com.denica.playlistmaker.settings.domain.api.SettingsRepository
import com.denica.playlistmaker.settings.domain.api.ShareRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    single<SongRepository> {
        SongRepositoryImpl(get())
    }
    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get())
    }
    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

    single<ShareRepository> {
        ShareRepositoryImpl(androidContext())
    }

}