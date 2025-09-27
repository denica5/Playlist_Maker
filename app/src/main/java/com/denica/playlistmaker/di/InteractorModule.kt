package com.denica.playlistmaker.di

import com.denica.playlistmaker.mediaLibrary.domain.FavouriteSongInteractor
import com.denica.playlistmaker.mediaLibrary.domain.FavouriteSongInteractorImpl
import com.denica.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.denica.playlistmaker.search.domain.api.SongInteractor
import com.denica.playlistmaker.search.domain.impl.SearchHistoryInteractorImpl
import com.denica.playlistmaker.search.domain.impl.SongInteractorImpl
import com.denica.playlistmaker.settings.domain.api.SettingsInteractor
import com.denica.playlistmaker.settings.domain.api.SharingInteractor
import com.denica.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.denica.playlistmaker.settings.domain.impl.SharingInteractorImpl
import org.koin.dsl.module

val interactorModule = module {
    single<SongInteractor> {
        SongInteractorImpl(get())
    }
    single<SearchHistoryInteractor> {
        SearchHistoryInteractorImpl(get())
    }

    single<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }
    single<SharingInteractor> {
        SharingInteractorImpl(get(), get())
    }

    single<FavouriteSongInteractor> {
        FavouriteSongInteractorImpl(get())
    }
}