package com.example.cryptoszd.ui.messageencryption.certificate

import co.zsmb.rainbowcake.base.RainbowCakeViewModel
import java.lang.Exception
import javax.inject.Inject

class CertificateViewModel @Inject constructor(
    private val certificatePresenter: CertificatePresenter
) : RainbowCakeViewModel<CertificateViewState>(Init) {

    fun getCertificate(certificateID: String) = execute {
        viewState = try {
            val uiCertificate = certificatePresenter.getCertificate(certificateID)
            Loaded(uiCertificate)
        } catch (e: Exception) {
            Error(e)
        }
    }
}