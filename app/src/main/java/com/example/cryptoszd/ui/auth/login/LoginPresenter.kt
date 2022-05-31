package com.example.cryptoszd.ui.auth.login

import co.zsmb.rainbowcake.withIOContext
import com.example.cryptoszd.domain.interactor.UserInteractor
import com.google.firebase.auth.AuthCredential
import javax.inject.Inject

class LoginPresenter @Inject constructor(
    private val userInteractor: UserInteractor
) {

    suspend fun emailAndPasswordLogin(email: String, password: String)  = withIOContext {
        userInteractor.emailAndPasswordLogin(email, password)
    }

    suspend fun signInWithCredential(credential: AuthCredential) = withIOContext{
        userInteractor.signInWithCredential(credential)
    }
}