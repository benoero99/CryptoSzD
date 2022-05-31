package com.example.cryptoszd.ui.main

import co.zsmb.rainbowcake.withIOContext
import com.example.cryptoszd.domain.interactor.UserInteractor
import com.example.cryptoszd.domain.model.DomainUser
import com.example.cryptoszd.ui.model.UiUser
import javax.inject.Inject

class MainPresenter @Inject constructor(
    private val userInteractor: UserInteractor
) {
    suspend fun getCurrentUiUser(): UiUser = withIOContext {
        userInteractor.getCurrentDomainUser().toUiUser()
    }

    private fun DomainUser.toUiUser() : UiUser {
        return UiUser(
            username = username,
            email = email,
            certificateID = certificateID,
            hasPasswordVault = cipherEx != null
        )
    }
}