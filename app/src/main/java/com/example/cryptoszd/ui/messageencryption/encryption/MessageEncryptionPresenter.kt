package com.example.cryptoszd.ui.messageencryption.encryption

import co.zsmb.rainbowcake.withIOContext
import com.example.cryptomod.BenoCrypto
import com.example.cryptoszd.domain.interactor.CertificateInteractor
import java.security.cert.X509Certificate
import javax.inject.Inject

class MessageEncryptionPresenter @Inject constructor(
    private val certificateInteractor: CertificateInteractor
) {

    suspend fun getCertificateEmailAndCN(certificateString: String): MutableList<String> = withIOContext {
        certificateInteractor.getCnAndEmailFromCert(certificateString)
    }

    suspend fun encryptMessage(certificateString: String, plainText: String) = withIOContext {
        certificateInteractor.encryptMessage(certificateString, plainText)
    }

    suspend fun decryptMessage(certificateString: String, cipherText: String) = withIOContext {
        certificateInteractor.decryptMessage(certificateString, cipherText)
    }
}