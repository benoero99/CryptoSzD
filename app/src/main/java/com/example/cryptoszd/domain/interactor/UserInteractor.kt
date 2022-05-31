package com.example.cryptoszd.domain.interactor

import com.example.cryptoszd.domain.model.DomainCertificate
import com.example.cryptoszd.domain.model.DomainUser
import com.example.cryptoszd.network.DbReference
import com.example.cryptoszd.network.NetworkDataSource
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.ktx.userProfileChangeRequest
import javax.inject.Inject

// User related business logic
class UserInteractor @Inject constructor(
    private val networkDataSource: NetworkDataSource
) {
    suspend fun emailAndPasswordLogin(email: String, password: String) {
        networkDataSource.emailAndPasswordLogin(email, password)
    }

    suspend fun createUserWithEmailAndPassword(email: String, password: String, name: String) {
        val profileUpdates = userProfileChangeRequest {
            displayName = name
        }
        networkDataSource.createUserWithEmailAndPassword(email, password, profileUpdates)

    }

    suspend fun getCurrentDomainUser(): DomainUser {
        return networkDataSource.getCurrentDomainUser()
    }

    suspend fun signInWithCredential(credential: AuthCredential) {
        networkDataSource.signInWithCredential(credential)
    }

    suspend fun createCertificate(commonName: String, countryCode: String, locality: String, email: String) {
        networkDataSource.createCertificate(commonName, countryCode, locality, email)
    }


}