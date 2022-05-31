package com.example.cryptoszd.domain.interactor

import com.example.cryptomod.BenoCrypto
import com.example.cryptoszd.domain.model.DomainPassword
import com.example.cryptoszd.network.NetworkDataSource
import com.example.cryptoszd.ui.model.UiPassword
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.lang.Exception
import javax.inject.Inject

class VaultInteractor @Inject constructor(
    private val networkDataSource: NetworkDataSource
) {

    suspend fun createVault(password: CharArray) {
        val plainText: String
        try {
            plainText = Firebase.auth.currentUser?.email!!
        } catch (e: Exception) {
            throw Exception("Email cannot be null!")
        }
        val cipherText = BenoCrypto().encryptTextByPassword(plainText, password)
        networkDataSource.setCipherEx(cipherText)
    }

    suspend fun openVault(password: CharArray): MutableList<DomainPassword> {
        if(password.isEmpty()) {
            throw Exception("Password cannot be empty")
        }
        val expected: String
        try {
            expected = Firebase.auth.currentUser?.email!!
        } catch (e: Exception) {
            throw Exception("Email cannot be null!")
        }
        val cipherText = networkDataSource.getCipherEx()
        val plainText = BenoCrypto().decryptTextByPassword(cipherText, password)
        if(expected != plainText) {
            throw Exception("Invalid Password")
        }
        val encryptedList = networkDataSource.getPasswords()
        val decryptedList = mutableListOf<DomainPassword>()
        for(element in encryptedList) {
            decryptedList.add(
                DomainPassword(
                    website = BenoCrypto().decryptTextByPassword(element.website,password ),
                    password = BenoCrypto().decryptTextByPassword(element.password,password )
                ))
        }
        return decryptedList
    }

    suspend fun savePasswords(passwordList: MutableList<UiPassword>, password: CharArray) {
        val encryptedList = mutableListOf<DomainPassword>()
        for (element in passwordList) {
            encryptedList.add(
                DomainPassword(
                    website = BenoCrypto().encryptTextByPassword(element.website,password ),
                    password = BenoCrypto().encryptTextByPassword(element.password,password )
                ))
        }
        networkDataSource.deletePasswordsDocuments()
        networkDataSource.savePasswords(encryptedList)
    }


}