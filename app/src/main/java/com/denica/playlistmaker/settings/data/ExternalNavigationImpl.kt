package com.denica.playlistmaker.settings.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity

import com.denica.playlistmaker.R
import com.denica.playlistmaker.settings.domain.ExternalNavigation
import com.denica.playlistmaker.settings.domain.model.EmailData

class ExternalNavigationImpl(val context: Context) : ExternalNavigation {

    override fun shareLink(shareLink: String): Intent {
        return Intent(Intent.ACTION_SEND).apply {
            putExtra(
                Intent.EXTRA_TEXT, context.getString(R.string.share_text_practicum)
            )
            setType("text/plain")
        }
    }

    override fun openLink(termsLink: String) : Intent {
        return Intent(
                Intent.ACTION_VIEW,
                Uri.parse(context.getString(R.string.user_agreement_practicum))
            )


    }

    override fun openEmail(supportLink: EmailData): Intent {
        val writeSupportIntent = Intent(Intent.ACTION_SENDTO)

        writeSupportIntent.apply {
            data = Uri.parse("mailto:")
            putExtra(
                Intent.EXTRA_EMAIL,
                arrayOf(context.getString(R.string.write_support_my_email_practicum))
            )
            putExtra(
                Intent.EXTRA_SUBJECT,
                context.getString(R.string.write_support_email_subject_practicum)
            )
            putExtra(
                Intent.EXTRA_TEXT, context.getString(R.string.write_support_email_body_practicum)
            )
        }
        return writeSupportIntent
    }
}