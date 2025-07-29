package com.denica.playlistmaker.settings.domain.impl

import android.content.Intent
import com.denica.playlistmaker.settings.domain.ExternalNavigation
import com.denica.playlistmaker.settings.domain.api.ShareRepository
import com.denica.playlistmaker.settings.domain.api.SharingInteractor
import com.denica.playlistmaker.settings.domain.model.EmailData

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigation,
    private val repository: ShareRepository
) : SharingInteractor {
    override fun shareApp(): Intent = externalNavigator.shareLink(getShareAppLink())


    override fun openTerms(): Intent =
        externalNavigator.openLink(getTermsLink())


    override fun openSupport(): Intent =
        externalNavigator.openEmail(getSupportEmailData())


    private fun getShareAppLink(): String =
        repository.getShareAppLink()


    private fun getSupportEmailData(): EmailData =
        repository.getSupportEmailData()


    private fun getTermsLink(): String =
        repository.getTermsLink()

}