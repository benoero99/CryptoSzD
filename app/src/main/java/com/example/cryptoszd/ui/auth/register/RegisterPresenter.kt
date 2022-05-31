package com.example.cryptoszd.ui.auth.register

import co.zsmb.rainbowcake.withIOContext
import com.example.cryptoszd.domain.interactor.UserInteractor
import javax.inject.Inject

class RegisterPresenter @Inject constructor(
    private val userInteractor: UserInteractor
) {
    suspend fun createUserWithEmailAndPassword(email: String, password: String, name: String) = withIOContext {
        userInteractor.createUserWithEmailAndPassword(email, password, name)
    }
}