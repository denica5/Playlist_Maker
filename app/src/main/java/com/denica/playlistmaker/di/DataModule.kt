package com.denica.playlistmaker.di

import android.content.Context
import androidx.room.Room
import com.denica.playlistmaker.mediaLibrary.data.db.SongDao
import com.denica.playlistmaker.mediaLibrary.data.db.AppDatabase
import com.denica.playlistmaker.search.data.network.ITUNES_BASE_URL
import com.denica.playlistmaker.search.data.network.ItunesApi
import com.denica.playlistmaker.search.data.network.NetworkClient
import com.denica.playlistmaker.search.data.network.RetrofitNetworkClient
import com.denica.playlistmaker.search.data.storage.StorageClient
import com.denica.playlistmaker.search.domain.models.Song
import com.denica.playlistmaker.search.ui.SEARCH_HISTORY_KEY
import com.denica.playlistmaker.settings.data.ExternalNavigationImpl
import com.denica.playlistmaker.settings.domain.ExternalNavigation
import com.denica.playlistmaker.settings.domain.model.ThemeSettings
import com.denica.playlistmaker.settings.ui.DARK_THEME_MODE_KEY
import com.denica.playlistmaker.settings.ui.PLAYLIST_MAKER_PREFERENCES
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {
    single<ItunesApi> {
        Retrofit.Builder()
            .baseUrl(ITUNES_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesApi::class.java)
    }
    single<NetworkClient> {
        RetrofitNetworkClient(get(), androidContext())
    }
    single<StorageClient<ArrayList<Song>>> {
        com.denica.playlistmaker.search.data.storage.PrefsStorageClient(
            SEARCH_HISTORY_KEY,
            object : TypeToken<ArrayList<Song>>() {}.type,
            get(),
            get()
        )
    }
    single<com.denica.playlistmaker.settings.data.StorageClient<ThemeSettings>> {
        com.denica.playlistmaker.settings.data.storage.PrefsStorageClient(
            DARK_THEME_MODE_KEY,
            object : TypeToken<ThemeSettings>() {}.type,
            get(),
            get()
        )
    }

    single {
        androidContext().getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, Context.MODE_PRIVATE)
    }
    factory {
        Gson()
    }

    single<ExternalNavigation> {
        ExternalNavigationImpl(androidContext())
    }

    single<AppDatabase> {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "FavouriteSongDatabase.db")
            .build()
    }
    single<SongDao>{
        get<AppDatabase>().songDao()
    }
}