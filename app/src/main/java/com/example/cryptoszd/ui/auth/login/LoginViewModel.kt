package com.example.cryptoszd.ui.auth.login

import androidx.fragment.app.FragmentActivity
import co.zsmb.rainbowcake.base.RainbowCakeViewModel
import com.google.firebase.auth.*
import java.lang.Exception
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val loginPresenter: LoginPresenter
) : RainbowCakeViewModel<LoginViewState>(DefaultState) {

    fun defaultState() = execute {
        viewState = DefaultState
    }
    fun firebaseAuthWithGoogle(idToken: String) = execute {
        viewState = Loading
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        viewState = try {
            loginPresenter.signInWithCredential(credential)
            Successful
        } catch (e : Exception) {
            Failure(e)
        }

    }

    fun emailAndPasswordLogin(email: String, password: String) = execute {
        viewState = Loading
        viewState = try {
            loginPresenter.emailAndPasswordLogin(email, password)
            Successful
        } catch (e: Exception) {
            Failure(e)
        }
    }
}