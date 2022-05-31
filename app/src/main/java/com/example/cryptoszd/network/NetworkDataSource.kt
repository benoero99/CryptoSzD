package com.example.cryptoszd.network

import android.util.Log
import com.example.cryptomod.BenoCrypto
import com.example.cryptoszd.domain.model.DomainCertificate
import com.example.cryptoszd.domain.model.DomainPassword
import com.example.cryptoszd.domain.model.DomainUser
import com.example.cryptoszd.ui.model.UiCertificate
import com.example.cryptoszd.ui.model.UiCertificateList
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.security.InvalidKeyException
import java.security.PublicKey
import java.security.SignatureException
import java.security.cert.CertificateExpiredException
import java.security.cert.X509Certificate
import javax.crypto.IllegalBlockSizeException
import javax.inject.Inject

class NetworkDataSource @Inject constructor(
    private val firebaseAccess: FirebaseAccess,
    private val collection: DbReference.Collection,
    private val field: DbReference.Field
)  {
    suspend fun emailAndPasswordLogin(email: String, password: String) {
        firebaseAccess.emailAndPasswordLogin(email, password)
    }

     suspend fun createUserWithEmailAndPassword(email: String, password: String, profileUpdates: UserProfileChangeRequest) {
         firebaseAccess.createUserWithEmailAndPassword(email, password, profileUpdates)
    }

    suspend fun getCurrentDomainUser() : DomainUser {
        return firebaseAccess.getDocumentByID(collection.USERS, Firebase.auth.currentUser!!.uid).let {
            DomainUser(
                username = it.get(DbReference.Field.USERNAME) as String,
                email =  it.get(DbReference.Field.EMAIL) as String,
                cipherEx = it.get(DbReference.Field.CIPHEREX) as String?,
                certificateID = it.get(DbReference.Field.CERTIFICATEID) as String?
            )
        }
    }

    suspend fun signInWithCredential(credential: AuthCredential) {
        firebaseAccess.signInWithCredential(credential)
    }

    suspend fun createCertificate(commonName: String, countryCode: String, locality: String, email: String) {
        firebaseAccess.createCertificate(commonName,countryCode,locality,email)
    }

    suspend fun setCipherEx(cipherEx: String) {
        val data = hashMapOf(
            field.CIPHEREX to cipherEx
        )
        firebaseAccess.updateDocumentFields(collection.USERS,Firebase.auth.currentUser!!.uid, data)
    }

    suspend fun getCipherEx(): String {
        val snapshot = firebaseAccess.getDocumentByID(collection.USERS, Firebase.auth.currentUser!!.uid)
        return snapshot.get(field.CIPHEREX) as String
    }

    suspend fun getPasswords(): MutableList<DomainPassword> {
        val snapshot = firebaseAccess.getNestedCollectionDocuments(collection.USERS)
        val list = mutableListOf<DomainPassword>()
        for(document in snapshot) {
            list.add(
                DomainPassword(
                    website = document.get(field.WEBSITE) as String,
                    password = document.get(field.PASSWORD) as String
            ))
        }
        return list
    }

    suspend fun savePasswords(encryptedList: MutableList<DomainPassword>) {
        for(element in encryptedList) {
            firebaseAccess.setDocumentToPasswordCollection(element)
        }
    }

    suspend fun deletePasswordsDocuments() {
        val snapshot = firebaseAccess.getNestedCollectionDocuments(collection.USERS)
        for(document in snapshot) {
            firebaseAccess.deleteDocumentFromPasswordCollection(document.id)
        }
    }

    suspend fun getCertificate(certificateID: String): DomainCertificate {
        val snapshot = firebaseAccess.getDocumentByID(collection.CERTIFICATE, certificateID)
        val pemCertificateString = snapshot.get(field.CERTIFICATE) as String
        val cert = BenoCrypto().pemStringToCert(pemCertificateString) as X509Certificate
        val publicKey = getServerPublicKey()
        var expired = false
        var valid = true
        try {
            cert.verify(publicKey)
            cert.checkValidity()
        } catch (e: SignatureException) {
            valid = false
        } catch (e: IllegalBlockSizeException) {
            valid = false
        } catch (e: CertificateExpiredException) {
            expired = true
        }


//        val validity = BenoCrypto().verifyCertificate(cert, publicKey, cert.signature)
        return DomainCertificate(
            certificate = cert,
            valid = valid,
            expired = expired
        )
    }

    suspend fun getServerPublicKey() : PublicKey {
        val pemServerPublicKey = firebaseAccess.getDocuments(collection.SECRETKEY).documents[0].get(field.PUBLICKEY) as String
        return BenoCrypto().pemStringToPublicKey(pemServerPublicKey)
    }

    suspend fun getCertificates(): MutableList<UiCertificateList> {
        val query = firebaseAccess.getDocuments(collection.CERTIFICATE)
        val list = mutableListOf<UiCertificateList>()
        for(document in query) {
            list.add(
                UiCertificateList(
                    certificateID = document.id,
                    commonName = document.get(field.COMMONNAME) as String,
                    countryCode = document.get(field.COUNTRYCODE) as String,
                    locality = document.get(field.LOCALITY) as String,
                    email = document.get(field.EMAIL) as String,
                ))
        }
        return list
    }

    suspend fun getUserPrivateKey(): String {
        val certificateID =
            firebaseAccess.getDocumentByID(collection.USERS, Firebase.auth.currentUser!!.uid).get(field.CERTIFICATEID)
                ?: throw Exception("You don't have a certificate yet")
        return firebaseAccess.getDocumentByID(collection.CERTIFICATE, certificateID as String).get(field.PRIVATEKEY) as String
    }


}