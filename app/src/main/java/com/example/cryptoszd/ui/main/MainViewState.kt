package com.example.cryptoszd.ui.main

import com.example.cryptoszd.ui.model.UiUser
import java.lang.Exception

sealed class MainViewState

object Init : MainViewState()
object GiveMasterPassword : MainViewState()
object CreateMasterPassword : MainViewState()
data class Loaded(val user: UiUser) : MainViewState()

object Loading : MainViewState()

data class Error(val exception: Exception) : MainViewState()