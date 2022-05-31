package com.example.cryptoszd.ui.passwordvault.passwordvault

import co.zsmb.rainbowcake.withIOContext
import com.example.cryptoszd.domain.interactor.VaultInteractor
import com.example.cryptoszd.ui.model.UiPassword
import javax.inject.Inject

class PasswordVaultPresenter @Inject constructor(
    private val vaultInteractor: VaultInteractor
){

    suspend fun savePasswords(passwordList: MutableList<UiPassword>, password: CharArray) = withIOContext {
        vaultInteractor.savePasswords(passwordList, password)
    }
}