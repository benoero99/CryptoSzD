package com.example.cryptoszd.ui.passwordvault.dialog.openvault

import co.zsmb.rainbowcake.withIOContext
import com.example.cryptoszd.domain.interactor.VaultInteractor
import com.example.cryptoszd.domain.model.DomainPassword
import com.example.cryptoszd.ui.model.UiPassword
import javax.inject.Inject

class OpenVaultPresenter @Inject constructor(
    private val vaultInteractor: VaultInteractor
) {

    suspend fun openVault(password: CharArray): MutableList<UiPassword> = withIOContext {
        val domainList = vaultInteractor.openVault(password)
        val uiList= mutableListOf<UiPassword>()
        domainList.forEach { element ->
            uiList.add(element.toUiPassword())
        }
        uiList
    }

    private fun DomainPassword.toUiPassword(): UiPassword {
        return UiPassword(
            website = website,
            password = password
        )
    }

}