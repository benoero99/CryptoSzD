package com.example.cryptoszd.ui.passwordvault.dialog.createvault

import co.zsmb.rainbowcake.base.RainbowCakeViewModel
import javax.inject.Inject

class CreateVaultViewModel @Inject constructor(
    private val createVaultPresenter: CreateVaultPresenter
): RainbowCakeViewModel<CreateVaultViewState>(Default) {

    fun createVault(password: CharArray) = execute {
        viewState = Loading
        viewState = try {
            createVaultPresenter.createVault(password)
            Success
        } catch (e: Exception) {
            Error(e)
        }

    }


}