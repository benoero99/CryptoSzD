package com.example.cryptoszd.ui.auth.register

import java.lang.Exception

sealed class RegisterViewState

object DefaultState : RegisterViewState()

object Loading : RegisterViewState()

class Failure(val exception: Exception) : RegisterViewState()

object Success : RegisterViewState()