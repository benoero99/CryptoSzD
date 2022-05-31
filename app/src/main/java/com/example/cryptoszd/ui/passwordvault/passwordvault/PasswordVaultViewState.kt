package com.example.cryptoszd.ui.passwordvault.passwordvault

sealed class PasswordVaultViewState

object Loading : PasswordVaultViewState()
object Default : PasswordVaultViewState()
object Success : PasswordVaultViewState()
data class Error(val exception: Exception) : PasswordVaultViewState()