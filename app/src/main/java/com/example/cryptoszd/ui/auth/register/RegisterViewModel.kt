package com.example.cryptoszd.ui.auth.register

import co.zsmb.rainbowcake.base.RainbowCakeViewModel

import java.lang.Exception
import javax.inject.Inject

class RegisterViewModel @Inject constructor(
    private val registerPresenter: RegisterPresenter
) : RainbowCakeViewModel<RegisterViewState>(DefaultState) {

    fun defaultState() {
        viewState = DefaultState
    }
    fun emailAndPasswordRegister(email: String, password: String, name: String) = execute {
        viewState = Loading

        viewState = try {
            registerPresenter.createUserWithEmailAndPassword(email, password, name)
            Success
        } catch (e: Exception) {
            Failure(e)
        }
    }
}