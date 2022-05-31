package com.example.cryptoszd.ui.fileencryption

import co.zsmb.rainbowcake.base.RainbowCakeViewModel
import java.io.File
import java.lang.Exception
import javax.inject.Inject

class FileEncryptionViewModel @Inject constructor(
    private val fileEncryptionPresenter: FileEncryptionPresenter
): RainbowCakeViewModel<FileEncryptionViewState>(Default) {

    fun encrypt(password: CharArray, file: File, outputFile: File) = execute {
        viewState = Loading
        viewState = try {
            fileEncryptionPresenter.encrypt(password, file, outputFile)
            Success
        } catch (e: Exception) {
            Error(e)
        }
    }

    fun decrypt(password: CharArray, file: File, outputFile: File) = execute {
        viewState = Loading
        viewState = try {
            fileEncryptionPresenter.decrypt(password, file, outputFile)
            Success
        } catch (e: Exception) {
            Error(e)
        }
    }
}