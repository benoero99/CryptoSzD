package com.example.cryptoszd.ui.messageencryption.encryption

import co.zsmb.rainbowcake.base.RainbowCakeViewModel
import java.lang.Exception
import java.security.SignatureException
import java.security.cert.CertificateExpiredException
import javax.crypto.IllegalBlockSizeException
import javax.inject.Inject

class MessageEncryptionViewModel @Inject constructor(
    private val messageEncryptionPresenter: MessageEncryptionPresenter
): RainbowCakeViewModel<MessageEncryptionViewState>(Default) {

    fun getPastedCertificate(certificateString: String) = execute {
        viewState = Loading
        viewState = try {
            val stringList = messageEncryptionPresenter.getCertificateEmailAndCN(certificateString)
            SuccessfulPaste(stringList[0], stringList[1])
        } catch (e: SignatureException) {
            Error(Exception("Pasted certificate is invalid!"))
        } catch (e: IllegalBlockSizeException) {
            Error(Exception("Pasted certificate is invalid!"))
        } catch (e: CertificateExpiredException) {
            Error(Exception("Pasted certificate is expired!"))
        } catch (e: Exception) {
            Error(Exception("Invalid input"))
        }
    }

    fun encryptMessage(certificateString: String, plainText: String) = execute {
        viewState = Loading
        viewState = try {
            val encryptedMessage = messageEncryptionPresenter.encryptMessage(certificateString, plainText)
            SuccessfulEncrypt(encryptedMessage)
        } catch (e: Exception) {
            Error(e)
        }
    }

    fun decryptMessage(certificateString: String, cipherText: String) = execute {
        viewState = Loading
        viewState = try {
            val decryptedMessage = messageEncryptionPresenter.decryptMessage(certificateString, cipherText)
            SuccessfulDecrypt(decryptedMessage)
        } catch (e: Exception) {
            Error(e)
        }
    }

    fun default() = execute {
        viewState = Default
    }


}