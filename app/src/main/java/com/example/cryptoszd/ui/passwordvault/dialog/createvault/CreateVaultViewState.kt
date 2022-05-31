package com.example.cryptoszd.ui.passwordvault.dialog.createvault

sealed class CreateVaultViewState

object Default : CreateVaultViewState()
object Loading : CreateVaultViewState()
object Success: CreateVaultViewState()
data class Error(val exception: Exception) : CreateVaultViewState()