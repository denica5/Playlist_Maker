package com.denica.playlistmaker.settings.domain.api

import com.denica.playlistmaker.settings.domain.model.EmailData

interface ShareRepository {
    fun getShareAppLink(): String

    fun getSupportEmailData(): EmailData

    fun getTermsLink(): String
}