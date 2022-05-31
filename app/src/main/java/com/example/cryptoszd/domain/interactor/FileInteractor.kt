package com.example.cryptoszd.domain.interactor

import com.example.cryptomod.BenoCrypto
import java.io.File
import javax.inject.Inject

class FileInteractor @Inject constructor() {

    fun encrypt(password: CharArray, file: File, outputFile: File) {
        BenoCrypto().encryptFileByPassword(file, password, outputFile)
    }

    fun decrypt(password: CharArray, file: File, outputFile: File) {
        BenoCrypto().decryptFileByPassword(file, password, outputFile)
    }


}