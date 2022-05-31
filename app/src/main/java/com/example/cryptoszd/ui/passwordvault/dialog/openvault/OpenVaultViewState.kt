package com.example.cryptoszd.ui.passwordvault.dialog.openvault

import com.example.cryptoszd.ui.model.UiPassword

sealed class OpenVaultViewState

object Default : OpenVaultViewState()
object Loading : OpenVaultViewState()
data class Success(val passwords: MutableList<UiPassword>): OpenVaultViewState()
data class Error(val exception: Exception) : OpenVaultViewState()
