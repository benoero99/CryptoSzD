package com.example.cryptoszd.ui.messageencryption.certificate

import com.example.cryptoszd.ui.model.UiCertificate
import java.lang.Exception

sealed class CertificateViewState

object Init : CertificateViewState()
data class Loaded(var uiCertificate: UiCertificate) : CertificateViewState()
data class Error(var exception: Exception) : CertificateViewState()
