package com.example.cryptoszd.ui.messageencryption.listofcertificates

import com.example.cryptoszd.ui.model.UiCertificateList
import java.lang.Exception

sealed class ListOfCertificatesViewState

object Init : ListOfCertificatesViewState()
object Default : ListOfCertificatesViewState()
data class Loaded(val list: MutableList<UiCertificateList>) : ListOfCertificatesViewState()
data class Error(val exception: Exception) : ListOfCertificatesViewState()