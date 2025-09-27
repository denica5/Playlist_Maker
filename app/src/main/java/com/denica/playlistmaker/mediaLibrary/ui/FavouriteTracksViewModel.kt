package com.denica.playlistmaker.mediaLibrary.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denica.playlistmaker.mediaLibrary.domain.FavouriteSongInteractor
import com.denica.playlistmaker.search.domain.models.Song
import kotlinx.coroutines.launch

class FavouriteTracksViewModel(
    private val favouriteSongInteractor: FavouriteSongInteractor
) : ViewModel() {

    private val favouriteStateLiveData = MutableLiveData<FavouriteTracksState>()

    fun observeFavouriteState(): LiveData<FavouriteTracksState> = favouriteStateLiveData

    fun getFavouriteSongs() {
        renderState(FavouriteTracksState.Loading)
        viewModelScope.launch {
            favouriteSongInteractor.getFavouriteSongs().collect { songs ->
                processResult(songs)
            }
        }
    }

    private fun processResult(songs: List<Song>) {
        if (songs.isEmpty()) {
            renderState(FavouriteTracksState.Empty("Empty"))
        } else {
            renderState(FavouriteTracksState.Content(songs))
        }
    }

    private fun renderState(state: FavouriteTracksState) {
        favouriteStateLiveData.postValue(state)
    }
}