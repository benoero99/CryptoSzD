package com.example.cryptoszd.ui.main


import android.util.Base64
import co.zsmb.rainbowcake.base.RainbowCakeViewModel
import com.example.cryptomod.BenoCrypto
import com.example.cryptoszd.ui.model.UiUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.lang.Exception
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val mainPresenter: MainPresenter
) : RainbowCakeViewModel<MainViewState>(Init) {
    val user = Firebase.auth.currentUser
    lateinit var username: String
    lateinit var email: String

    fun init() = execute {
        viewState = try {
            val uiUser = mainPresenter.getCurrentUiUser()
            Loaded(uiUser)
        } catch (e: Exception) {
            Error(e)
        }
    }

//
//    fun getUserPublicKeyString() = execute {
//        Firebase.firestore.collection("Users").document(user?.uid.toString()).get().addOnSuccessListener { document ->
//            if(document != null) {
//                val certificateID = document.get("certificate")
//                if(certificateID != null) {
//                    Firebase.firestore.collection("Certificate").document(certificateID.toString()).get().addOnCompleteListener { task ->
//                        viewState = if (task.isSuccessful) {
//                            val publicKeyPemString = task.result.get("publicKey") as String
//                            val publicKey = BenoCrypto().pemToPublicKey(publicKeyPemString)
//                            val publicKeyString = Base64.encodeToString(publicKey.encoded, Base64.DEFAULT)
//                            GotPublicKey(publicKeyString)
//                        } else {
//                            Error(task.exception!!)
//                        }
//                    }
//                } else {
//                    //viewState = NoCertificate
//                }
//            }
//        }.addOnFailureListener { exception ->
//            viewState = Error(exception)
//        }
//    }

    fun default(user: UiUser) {
        viewState = Loaded(user)
    }

    fun userHasPasswordVault(user: UiUser) {
        viewState = if(user.hasPasswordVault) {
            GiveMasterPassword
        } else {
            CreateMasterPassword
        }
    }


}