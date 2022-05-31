package com.example.cryptoszd.ui.messageencryption.createcertificate

import java.lang.Exception

sealed class CertificateViewState

object Default : CertificateViewState()

data class Failed(var exception: Exception) : CertificateViewState()

object Loading : CertificateViewState()

object Success : CertificateViewState()