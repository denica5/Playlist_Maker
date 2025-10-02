package com.denica.playlistmaker.mediaLibrary.ui.playlist

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denica.playlistmaker.mediaLibrary.domain.DbPlaylistInteractor
import com.denica.playlistmaker.mediaLibrary.domain.Playlist
import kotlinx.coroutines.launch

class CreatePlaylistViewModel(val playlistInteractor: DbPlaylistInteractor) : ViewModel() {

    private val isImagePicked: MutableLiveData<Pair<Boolean, String>> = MutableLiveData(Pair(false, ""))
    fun isImagePicked(): LiveData<Pair<Boolean, String>> = isImagePicked

    fun pickImage(uri: Uri) {
        isImagePicked.postValue(Pair(true, uri.toString()))
    }

    fun createPlaylist(name: String, description: String, imagePath: String): Playlist {
        return Playlist(name = name, description = description, imagePath = imagePath)
    }

    fun addPlaylist(name: String, description: String, imagePath: String) {
        val playlist = createPlaylist(name, description, imagePath)
        viewModelScope.launch { playlistInteractor.insertPlaylist(playlist) }
    }
}