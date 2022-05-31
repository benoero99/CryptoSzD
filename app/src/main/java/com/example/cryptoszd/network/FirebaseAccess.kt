package com.example.cryptoszd.network

import com.example.cryptoszd.domain.model.DomainUser
import com.google.firebase.auth.*
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import javax.inject.Inject

class FirebaseAccess @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val functions: FirebaseFunctions
    )  {

    suspend fun createUserWithEmailAndPassword(email: String, password: String, profileUpdates: UserProfileChangeRequest)  {
        try {
            auth.createUserWithEmailAndPassword(email, password).await()
            updateProfile(auth.currentUser!!, profileUpdates)
            val data = DomainUser(
                username = auth.currentUser?.displayName!!,
                email = email,
                cipherEx = null,
                certificateID = null
            )
            setDocumentByID(DbReference.Collection.USERS, auth.currentUser!!.uid,data)
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun signInWithCredential(credential: AuthCredential) {
        try {
            val result = auth.signInWithCredential(credential).await()
            if(result.additionalUserInfo!!.isNewUser) {
                var googleEmail = ""
                val user = auth.currentUser
                user?.let {
                    for (profile in it.providerData) {
                        googleEmail = profile.email.toString()
                    }
                }
                user?.updateEmail(googleEmail)
                val data = DomainUser(
                    username = auth.currentUser?.displayName!!,
                    email = googleEmail,
                    cipherEx = null,
                    certificateID = null
                )
                setDocumentByID(DbReference.Collection.USERS, auth.currentUser!!.uid,data)
            }
        } catch (e : Exception) {
            throw e
        }
    }

    suspend fun emailAndPasswordLogin(email: String, password: String) {
        try {
            auth.signInWithEmailAndPassword(email, password).await()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun updateProfile(user: FirebaseUser, profileUpdates: UserProfileChangeRequest) {
        try {
            user.updateProfile(profileUpdates).await()
        } catch (e : Exception) {
            throw e
        }
    }

    suspend fun getDocuments(collectionRef: String): QuerySnapshot {
        return try {
            firestore.collection(collectionRef).get().await()
        } catch (e : Exception) {
            throw Exception("Something went wrong")
        }
    }

    suspend fun getDocumentByID(collectionRef: String, documentID: String ) : DocumentSnapshot {
        return try {
            firestore.collection(collectionRef).document(documentID).get().await()
        } catch (e : Exception) {
            throw Exception("Something went wrong")
        }
    }

    suspend fun setDocument(collectionRef: String,  data: Any)  {
        try {
            firestore.collection(collectionRef).document().set(data).await()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun setDocumentByID(collectionRef: String, documentID: String, data: Any)  {
        try {
            firestore.collection(collectionRef).document(documentID).set(data).await()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun setDocumentMergeByID(collectionRef: String, documentID: String, data: Any)  {
        try {
            firestore.collection(collectionRef).document(documentID).set(data, SetOptions.merge()).await()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun updateDocumentFields(collectionRef: String, documentID: String,fields: Map<String, Any>) {
        try {
            firestore.collection(collectionRef).document(documentID).update(fields).await()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun createCertificate(commonName: String, countryCode: String, locality: String, email: String) {
        val data = hashMapOf(
            "commonName" to commonName,
            "countryCode" to countryCode,
            "locality" to locality,
            "email" to email
        )
        try {
            functions
                .getHttpsCallable("createCertificate")
                .call(data)
                .await()

        } catch (e: Exception) {
            throw e
        }

    }

    suspend fun getNestedCollectionDocuments(collectionRef: String): QuerySnapshot {
        return firestore.collection(collectionRef).document(auth.currentUser!!.uid)
            .collection("Password").get().await()
    }

    suspend fun deleteDocumentFromPasswordCollection(documentID: String) {
        firestore.collection("Users").document(auth.currentUser!!.uid).collection("Password").document(documentID).delete().await()
    }

    suspend fun setDocumentToPasswordCollection(data: Any) {
        firestore.collection("Users").document(auth.currentUser!!.uid).collection("Password").document().set(data).await()
    }

}