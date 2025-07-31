package com.denica.playlistmaker.settings.data

import android.content.Context
import com.denica.playlistmaker.R
import com.denica.playlistmaker.settings.domain.api.ShareRepository
import com.denica.playlistmaker.settings.domain.model.EmailData

class ShareRepositoryImpl(val context: Context) : ShareRepository {
    override fun getShareAppLink(): String =
        context.getString(R.string.share_text_practicum)


    override fun getSupportEmailData(): EmailData =
        EmailData(
            context.getString(R.string.write_support_my_email_practicum),
            context.getString(R.string.write_support_email_subject_practicum),
            context.getString(R.string.write_support_email_body_practicum)
        )


    override fun getTermsLink(): String = context.getString(R.string.user_agreement_practicum)
}