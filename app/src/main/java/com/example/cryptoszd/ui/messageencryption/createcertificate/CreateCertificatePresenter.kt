package com.example.cryptoszd.ui.messageencryption.createcertificate

import co.zsmb.rainbowcake.withIOContext
import com.example.cryptoszd.domain.interactor.UserInteractor
import javax.inject.Inject

class CreateCertificatePresenter @Inject constructor(
    private val userInteractor: UserInteractor
) {
    suspend fun createCertificate(commonName: String, countryCode: String, locality: String, email: String) = withIOContext {
        userInteractor.createCertificate(commonName, countryCode, locality, email)
    }

}