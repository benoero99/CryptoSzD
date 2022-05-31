package com.example.cryptoszd.ui.passwordvault.passwordvault

import co.zsmb.rainbowcake.base.RainbowCakeViewModel
import com.example.cryptoszd.ui.model.UiPassword
import java.lang.Exception
import javax.inject.Inject

class PasswordVaultViewModel @Inject constructor(
    private val passwordVaultPresenter: PasswordVaultPresenter
): RainbowCakeViewModel<PasswordVaultViewState>(Default) {

    fun savePasswords(passwordList: MutableList<UiPassword>, password: CharArray) = execute {
        viewState = Loading
        viewState = try {
            passwordVaultPresenter.savePasswords(passwordList, password)
            Success
        } catch (e: Exception) {
            Error(e)
        }
    }


}