package com.denica.playlistmaker.mediaLibrary.ui.playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.denica.playlistmaker.mediaLibrary.domain.DbPlaylistInteractor
import com.denica.playlistmaker.mediaLibrary.domain.Playlist
import com.denica.playlistmaker.mediaLibrary.ui.playlist.createplaylist.CreatePlaylistViewModel
import kotlinx.coroutines.launch

class EditPlaylistViewmodel(
    override val playlistInteractor: DbPlaylistInteractor,
    val playlist: Playlist
) :
    CreatePlaylistViewModel(playlistInteractor) {
    private val editPlaylistState = MutableLiveData<EditPlaylistState>()
    fun getEditPlaylistState(): LiveData<EditPlaylistState> = editPlaylistState

    fun updatePlaylist(
        newPlaylistName: String,
        newPlaylistDescription: String,
        newPlaylistImagePath: String
    ) {
        viewModelScope.launch {
            val updatedPlaylist = Playlist(
                playlist.id,
                newPlaylistName,
                newPlaylistDescription,
                newPlaylistImagePath,
                playlist.trackIds,
                playlist.trackCount
            )
            playlistInteractor.updatePlaylist(updatedPlaylist)
        }
    }

    init {
        editPlaylistState.postValue(
            EditPlaylistState(
                playlist.name,
                playlist.description,
                playlist.imagePath
            )
        )
    }

}