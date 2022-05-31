package com.example.cryptoszd.ui.fileencryption

import co.zsmb.rainbowcake.withIOContext
import com.example.cryptoszd.domain.interactor.FileInteractor
import java.io.File
import javax.inject.Inject

class FileEncryptionPresenter @Inject constructor(
    private val fileInteractor: FileInteractor

){

    suspend fun encrypt(password: CharArray, file: File, outputFile: File) = withIOContext {
        fileInteractor.encrypt(password, file, outputFile)
    }

    suspend fun decrypt(password: CharArray, file: File, outputFile: File) = withIOContext {
        fileInteractor.decrypt(password, file, outputFile)
    }
}