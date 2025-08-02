package com.denica.playlistmaker.di

import com.denica.playlistmaker.mediaplayer.ui.MediaPlayerViewModel
import com.denica.playlistmaker.search.ui.SearchViewModel
import com.denica.playlistmaker.settings.ui.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { (previewUrl: String) ->
        MediaPlayerViewModel(previewUrl)
    }

    viewModel {
        SearchViewModel(get(), get())
    }
    viewModel {
        SettingsViewModel(get(), get())
    }
}