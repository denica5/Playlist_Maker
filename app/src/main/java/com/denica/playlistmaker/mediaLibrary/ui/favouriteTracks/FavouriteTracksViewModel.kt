package com.denica.playlistmaker.mediaLibrary.ui.favouriteTracks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denica.playlistmaker.mediaLibrary.domain.DbSongInteractor
import com.denica.playlistmaker.search.domain.models.Song
import kotlinx.coroutines.launch

class FavouriteTracksViewModel(
    private val dbSongInteractor: DbSongInteractor
) : ViewModel() {

    private val favouriteStateLiveData = MutableLiveData<FavouriteTracksState>()

    fun observeFavouriteState(): LiveData<FavouriteTracksState> = favouriteStateLiveData

    fun getFavouriteSongs() {
        renderState(FavouriteTracksState.Loading)
        viewModelScope.launch {
            dbSongInteractor.getFavouriteSongs().collect { songs ->
                processResult(songs)
            }
        }
    }

    private fun processResult(songs: List<Song>) {
        if (songs.isEmpty()) {
            renderState(FavouriteTracksState.Empty)
        } else {
            renderState(FavouriteTracksState.Content(songs))
        }
    }

    private fun renderState(state: FavouriteTracksState) {
        favouriteStateLiveData.postValue(state)
    }
}