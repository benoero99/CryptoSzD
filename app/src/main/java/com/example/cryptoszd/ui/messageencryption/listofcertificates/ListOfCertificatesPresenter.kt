package com.example.cryptoszd.ui.messageencryption.listofcertificates

import co.zsmb.rainbowcake.withIOContext
import com.example.cryptoszd.domain.interactor.CertificateInteractor
import com.example.cryptoszd.ui.model.UiCertificateList
import javax.inject.Inject

class ListOfCertificatesPresenter @Inject constructor(
    private val certificateInteractor: CertificateInteractor
){

    suspend fun loadCertificates(): MutableList<UiCertificateList> = withIOContext {
        certificateInteractor.getCertificates()
    }
}