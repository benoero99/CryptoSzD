package com.example.cryptoszd.ui.passwordvault.dialog.createvault

import co.zsmb.rainbowcake.withIOContext
import com.example.cryptoszd.domain.interactor.VaultInteractor
import javax.inject.Inject

class CreateVaultPresenter @Inject constructor(
    private val vaultInteractor: VaultInteractor
) {

    suspend fun createVault(password: CharArray) = withIOContext {
        vaultInteractor.createVault(password)
    }
}