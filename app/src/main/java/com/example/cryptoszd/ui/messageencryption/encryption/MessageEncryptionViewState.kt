package com.example.cryptoszd.ui.messageencryption.encryption

import java.lang.Exception

sealed class MessageEncryptionViewState

object Default : MessageEncryptionViewState()
data class SuccessfulPaste(val email: String, val commonName: String) : MessageEncryptionViewState()
data class SuccessfulEncrypt(val encryptedMessage: String) : MessageEncryptionViewState()
data class SuccessfulDecrypt(val decryptedMessage: String) : MessageEncryptionViewState()
data class Error(val exception: Exception) : MessageEncryptionViewState()
object Loading : MessageEncryptionViewState()