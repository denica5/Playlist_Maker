package com.denica.playlistmaker.creator

import android.content.Context
import com.denica.playlistmaker.search.data.SearchHistoryRepositoryImpl
import com.denica.playlistmaker.search.data.SongRepositoryImpl
import com.denica.playlistmaker.search.data.network.RetrofitNetworkClient
import com.denica.playlistmaker.search.data.storage.PrefsStorageClient
import com.denica.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.denica.playlistmaker.search.domain.api.SearchHistoryRepository
import com.denica.playlistmaker.search.domain.api.SongInteractor
import com.denica.playlistmaker.search.domain.api.SongRepository
import com.denica.playlistmaker.search.domain.impl.SearchHistoryInteractorImpl
import com.denica.playlistmaker.search.domain.impl.SongInteractorImpl
import com.denica.playlistmaker.search.domain.models.Song
import com.denica.playlistmaker.search.ui.SEARCH_HISTORY_KEY
import com.denica.playlistmaker.settings.data.ExternalNavigationImpl
import com.denica.playlistmaker.settings.data.SettingsRepositoryImpl
import com.denica.playlistmaker.settings.data.ShareRepositoryImpl
import com.denica.playlistmaker.settings.domain.ExternalNavigation
import com.denica.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.denica.playlistmaker.settings.domain.api.SettingsInteractor
import com.denica.playlistmaker.settings.domain.api.SettingsRepository
import com.denica.playlistmaker.settings.domain.api.ShareRepository
import com.denica.playlistmaker.settings.domain.api.SharingInteractor
import com.denica.playlistmaker.settings.domain.impl.SharingInteractorImpl
import com.denica.playlistmaker.settings.domain.model.ThemeSettings
import com.denica.playlistmaker.settings.ui.DARK
import com.denica.playlistmaker.settings.ui.DARK_THEME_MODE_KEY
import com.google.gson.reflect.TypeToken

object Creator {
    private fun getSongsRepository(): SongRepository {
        return SongRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideSongsInteractor(): SongInteractor {
        return SongInteractorImpl(getSongsRepository())
    }

    private fun getSearchHistoryRepository(context: Context): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(
            PrefsStorageClient<ArrayList<Song>>(
                context,
                SEARCH_HISTORY_KEY,
                object : TypeToken<ArrayList<Song>>() {}.type
            )
        )

    }

    fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(getSearchHistoryRepository(context))
    }

    private fun getSettingsRepository(context: Context): SettingsRepository {
        return SettingsRepositoryImpl(
            com.denica.playlistmaker.settings.data.storage.PrefsStorageClient<ThemeSettings>(
                context,
                DARK,
                object : TypeToken<ThemeSettings>() {}.type
            )
        )
    }

    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository(context))
    }

    private fun getShareRepository(context: Context): ShareRepository {
        return ShareRepositoryImpl(context = context)
    }

    private fun getExternalNavigation(context: Context): ExternalNavigation {
        return ExternalNavigationImpl(context)
    }

    fun provideShareInteractor(context: Context): SharingInteractor {
        return SharingInteractorImpl(getExternalNavigation(context), getShareRepository(context))
    }
}