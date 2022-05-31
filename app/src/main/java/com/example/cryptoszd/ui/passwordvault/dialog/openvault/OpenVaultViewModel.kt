package com.example.cryptoszd.ui.passwordvault.dialog.openvault

import co.zsmb.rainbowcake.base.RainbowCakeViewModel
import javax.inject.Inject

class OpenVaultViewModel @Inject constructor(
    private val openVaultPresenter: OpenVaultPresenter
): RainbowCakeViewModel<OpenVaultViewState>(Default) {

    fun openVault(password: CharArray) = execute {
        viewState = Loading
        viewState = try {
            val passwords = openVaultPresenter.openVault(password)
            Success(passwords)
        } catch (e: Exception) {
            Error(e)
        }
    }


}