package com.example.cryptoszd.ui.messageencryption.listofcertificates

import co.zsmb.rainbowcake.base.RainbowCakeViewModel
import com.example.cryptoszd.domain.model.DomainCertificate
import javax.inject.Inject

class ListOfCertificatesViewModel @Inject constructor(
    private val listOfCertificatesPresenter: ListOfCertificatesPresenter
) : RainbowCakeViewModel<ListOfCertificatesViewState>(Init) {

    fun loadCertificates() = execute {
        viewState = try {
            val list = listOfCertificatesPresenter.loadCertificates()
            Loaded(list)
        } catch (e: Exception) {
            Error(e)
        }
    }
}