package com.example.cryptoszd.ui.fileencryption

import java.lang.Exception

sealed class FileEncryptionViewState

object Default : FileEncryptionViewState()
object Loading : FileEncryptionViewState()
data class Error(val exception: Exception) : FileEncryptionViewState()
object Success : FileEncryptionViewState()