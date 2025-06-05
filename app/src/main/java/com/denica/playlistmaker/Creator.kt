package com.denica.playlistmaker

import com.denica.playlistmaker.data.SongRepositoryImpl
import com.denica.playlistmaker.data.dto.RetrofitNetworkClient
import com.denica.playlistmaker.domain.api.SongInteractor
import com.denica.playlistmaker.domain.api.SongRepository
import com.denica.playlistmaker.domain.impl.SongInteractorImpl

object Creator {
    private fun getSongsRepository(): SongRepository {
        return SongRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideSongsInteractor(): SongInteractor {
        return SongInteractorImpl(getSongsRepository())
    }
}