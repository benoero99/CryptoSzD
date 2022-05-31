package com.example.cryptoszd.ui.auth.login

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import co.zsmb.rainbowcake.base.RainbowCakeFragment
import co.zsmb.rainbowcake.dagger.getViewModelFromFactory
import co.zsmb.rainbowcake.extensions.exhaustive
import co.zsmb.rainbowcake.navigation.navigator
import com.example.cryptoszd.R
import com.example.cryptoszd.utils.SingleToast
import com.example.cryptoszd.ui.main.MainFragment
import com.example.cryptoszd.ui.auth.register.RegisterFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthException
import com.wang.avi.AVLoadingIndicatorView


class LoginFragment : RainbowCakeFragment<LoginViewState, LoginViewModel>() {
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    private lateinit var emailTIET: TextInputEditText
    private lateinit var emailTIL: TextInputLayout
    private lateinit var passwordTIET: TextInputEditText
    private lateinit var passwordTIL: TextInputLayout
    private lateinit var progressBar: AVLoadingIndicatorView
    private lateinit var loginButton: MaterialButton
    private lateinit var googleButton: MaterialButton
    private lateinit var registerButton: MaterialButton

    override fun provideViewModel() = getViewModelFromFactory()
    override fun getViewResource() = R.layout.fragment_login


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    // Successful login
                    val account = task.getResult(ApiException::class.java)!!
                    viewModel.firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    // Exception
                }
            } else {
                SingleToast.show(requireContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()


        loginButton.setOnClickListener {
            if(emailAndPasswordIsNotEmpty()) {
                viewModel.emailAndPasswordLogin(emailTIET.text.toString(), passwordTIET.text.toString())
            }

        }
        googleButton.setOnClickListener{
            googleLogin()
        }
        registerButton.setOnClickListener {
            navigator?.add(RegisterFragment())
        }
        emailTIL.editText?.addTextChangedListener {
            emailTIL.error = ""
            emailTIL.isErrorEnabled = false
        }
        passwordTIL.editText?.addTextChangedListener {
            passwordTIL.error = ""
            passwordTIL.isErrorEnabled = false
        }
    }

    private fun init() {
        emailTIET = requireView().findViewById(R.id.emailTIET)
        emailTIL = requireView().findViewById(R.id.registerEmailTIL)
        passwordTIET = requireView().findViewById(R.id.passwordTIET)
        passwordTIL = requireView().findViewById(R.id.registerPasswordTIL)
        progressBar = requireView().findViewById(R.id.progressBar)
        loginButton = requireView().findViewById(R.id.loginButton)
        googleButton = requireView().findViewById(R.id.googleButton)
        registerButton = requireView().findViewById(R.id.registerButton)
    }

    private fun emailAndPasswordIsNotEmpty(): Boolean {
        if(emailTIET.text.toString().isEmpty()) {
            emailTIL.error = getString(R.string.empty_email_error)
            emailTIET.requestFocus()
            return false
        }
        if(passwordTIET.text.toString().isEmpty()) {
            passwordTIL.error = getString(R.string.empty_password_error)
            passwordTIET.requestFocus()
            return false
        }
        return true
    }

    private fun googleLogin() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("879511913608-a0b77f272mnlftkfhfhjui8gk6skjet5.apps.googleusercontent.com")
                .requestEmail()
                .build()

        val googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        val signInIntent = googleSignInClient.signInIntent

        resultLauncher.launch(signInIntent)
    }

    override fun render(viewState: LoginViewState) {
        when (viewState) {
            DefaultState -> {
                progressBar.smoothToHide()
            }
            Loading -> {
                progressBar.smoothToShow()
            }
            is Failure -> {
                if (viewState.exception is FirebaseAuthException) {
                    when(viewState.exception.errorCode) {
                        "ERROR_INVALID_EMAIL" -> {
                            emailTIL.error = getString(R.string.bad_email_format)
                            emailTIL.requestFocus()
                        }
                        "ERROR_WRONG_PASSWORD" -> {
                            passwordTIL.error = getString(R.string.wrong_password)
                            passwordTIL.requestFocus()
                        }
                        "ERROR_USER_DISABLED" -> {
                            SingleToast.show(requireContext(), getString(R.string.account_disabled), Toast.LENGTH_SHORT)
                        }
                        "ERROR_USER_NOT_FOUND" -> {
                            emailTIL.error = getString(R.string.user_doesnt_exist_or_deleted)
                            emailTIL.requestFocus()
                        }
                        else -> SingleToast.show(requireContext(), viewState.exception.message, Toast.LENGTH_LONG)
                    }
                } else if(viewState.exception is FirebaseNetworkException) {
                    SingleToast.show(requireContext(), getString(R.string.network_error), Toast.LENGTH_LONG)
                } else if(viewState.exception is FirebaseTooManyRequestsException) {
                    SingleToast.show(requireContext(), getString(R.string.too_many_failed_login_attempt), Toast.LENGTH_LONG)
                } else {
                    SingleToast.show(requireContext(), viewState.exception.message, Toast.LENGTH_LONG)
                }
                viewModel.defaultState()
            }
            Successful -> {
                navigator?.replace(MainFragment())
            }
        }.exhaustive
    }
}