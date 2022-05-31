package com.example.cryptoszd.ui.messageencryption.createcertificate

import co.zsmb.rainbowcake.base.RainbowCakeViewModel
import java.io.IOException
import java.util.*
import javax.inject.Inject

class CreateCertificateViewModel @Inject constructor(
    private val certificatePresenter: CreateCertificatePresenter
) : RainbowCakeViewModel<CertificateViewState>(Default) {

    fun createCertificate(commonName: String, countryCode: String, locality: String, email: String) = execute {
        viewState = Loading
        if(!isOnline()) {
            viewState = Failed(Exception("Network error"))
            return@execute
        }
        viewState = try {
            certificatePresenter.createCertificate(commonName, countryCode, locality, email)
            Success
        } catch (e : Exception) {
            Failed(e)
        }
    }

    fun default() {
        viewState = Default
    }

    private fun isOnline(): Boolean {
        val runtime = Runtime.getRuntime()
        try {
            val ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8")
            val exitValue = ipProcess.waitFor()
            return exitValue == 0
        } catch (e: IOException) {
            viewState = Failed(e)
        } catch (e: InterruptedException) {
            viewState = Failed(e)
        }
        return false
    }
}