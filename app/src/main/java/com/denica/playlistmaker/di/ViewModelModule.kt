package com.denica.playlistmaker.di

import android.media.MediaPlayer
import com.denica.playlistmaker.mediaLibrary.ui.FavouriteTracksViewModel
import com.denica.playlistmaker.mediaLibrary.ui.PlaylistViewModel
import com.denica.playlistmaker.mediaplayer.ui.MediaPlayerViewModel
import com.denica.playlistmaker.search.ui.SearchViewModel
import com.denica.playlistmaker.settings.ui.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { (previewUrl: String) ->
        MediaPlayerViewModel(previewUrl, MediaPlayer())
    }

    viewModel {
        SearchViewModel(get(), get())
    }
    viewModel {
        SettingsViewModel(get(), get())
    }
    viewModel {
        FavouriteTracksViewModel()
    }
    viewModel {
        PlaylistViewModel()
    }
}
