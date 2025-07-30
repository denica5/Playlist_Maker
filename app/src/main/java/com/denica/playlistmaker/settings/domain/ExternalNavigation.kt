package com.denica.playlistmaker.settings.domain

import android.content.Intent
import com.denica.playlistmaker.settings.domain.model.EmailData

interface ExternalNavigation {
    fun shareLink(shareLink: String): Intent
    fun openLink(termsLink: String): Intent
    fun openEmail(supportLink: EmailData): Intent
}