package com.example.cryptoszd.ui.auth.login

import java.lang.Exception

sealed class LoginViewState

object Loading : LoginViewState()

object DefaultState : LoginViewState()

object Successful : LoginViewState()

data class Failure(val exception: Exception) : LoginViewState()