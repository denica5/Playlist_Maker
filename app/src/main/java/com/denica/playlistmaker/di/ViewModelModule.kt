package com.denica.playlistmaker.di

import com.denica.playlistmaker.mediaLibrary.domain.Playlist
import com.denica.playlistmaker.mediaLibrary.ui.favouriteTracks.FavouriteTracksViewModel
import com.denica.playlistmaker.mediaLibrary.ui.playlist.EditPlaylistViewmodel
import com.denica.playlistmaker.mediaLibrary.ui.playlist.createplaylist.CreatePlaylistViewModel
import com.denica.playlistmaker.mediaLibrary.ui.playlist.playlistdetail.PlaylistDetailViewModel
import com.denica.playlistmaker.mediaLibrary.ui.playlist.playlists.PlaylistViewModel
import com.denica.playlistmaker.mediaplayer.ui.MediaPlayerViewModel
import com.denica.playlistmaker.search.domain.models.Song
import com.denica.playlistmaker.search.ui.SearchViewModel
import com.denica.playlistmaker.settings.ui.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { (song: Song) ->
        MediaPlayerViewModel(song, get(), get())
    }

    viewModel {
        SearchViewModel(get(), get(), get())
    }
    viewModel {
        SettingsViewModel(get(), get())
    }
    viewModel {
        FavouriteTracksViewModel(get())
    }
    viewModel {
        PlaylistViewModel(get())
    }
    viewModel {
        CreatePlaylistViewModel(get())
    }
    viewModel { (playlist: Playlist) ->
        PlaylistDetailViewModel(get(), playlist)
    }
    viewModel {(playlist: Playlist) ->
        EditPlaylistViewmodel(get(), playlist)
    }
}
