package com.example.cryptoszd.ui.auth.register

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import co.zsmb.rainbowcake.base.RainbowCakeFragment
import co.zsmb.rainbowcake.dagger.getViewModelFromFactory
import co.zsmb.rainbowcake.extensions.exhaustive
import co.zsmb.rainbowcake.navigation.navigator
import com.example.cryptoszd.R
import com.example.cryptoszd.utils.SingleToast
import com.example.cryptoszd.ui.main.MainFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException

class RegisterFragment : RainbowCakeFragment<RegisterViewState, RegisterViewModel>() {
    private lateinit var firstNameTIET: TextInputEditText
    private lateinit var firstNameTIL: TextInputLayout
    private lateinit var lastNameTIET: TextInputEditText
    private lateinit var lastNameTIL: TextInputLayout
    private lateinit var registerEmailTIET: TextInputEditText
    private lateinit var registerEmailTIL: TextInputLayout
    private lateinit var registerConfirmEmailTIET: TextInputEditText
    private lateinit var registerConfirmEmailTIL: TextInputLayout
    private lateinit var registerPasswordTIET: TextInputEditText
    private lateinit var registerPasswordTIL: TextInputLayout
    private lateinit var registerConfirmPasswordTIET: TextInputEditText
    private lateinit var registerConfirmPasswordTIL: TextInputLayout
    private lateinit var registrationButton: MaterialButton
    private lateinit var registerProgressBar: ProgressBar

    override fun provideViewModel() = getViewModelFromFactory()
    override fun getViewResource() = R.layout.fragment_register

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()

        registrationButton.setOnClickListener {
            if(checkEditTexts()) {
                viewModel.emailAndPasswordRegister(
                        registerEmailTIL.editText?.text.toString(),
                        registerPasswordTIL.editText?.text.toString(),
                        firstNameTIL.editText?.text.toString() + " " + lastNameTIL.editText?.text.toString()
                )
            }
        }
        registerProgressBar = view.findViewById(R.id.registerProgressBar)
        addTextChangeListener(firstNameTIL)
        addTextChangeListener(lastNameTIL)
        addTextChangeListener(registerEmailTIL)
        addTextChangeListener(registerConfirmEmailTIL)
        addTextChangeListener(registerPasswordTIL)
        addTextChangeListener(registerConfirmPasswordTIL)
    }

    private fun init() {
        firstNameTIET = requireView().findViewById(R.id.firstNameTIET)
        firstNameTIL = requireView().findViewById(R.id.firstNameTIL)
        lastNameTIET = requireView().findViewById(R.id.lastNameTIET)
        lastNameTIL = requireView().findViewById(R.id.lastNameTIL)
        registerEmailTIET = requireView().findViewById(R.id.registerEmailTIET)
        registerEmailTIL = requireView().findViewById(R.id.registerEmailTIL)
        registerConfirmEmailTIET = requireView().findViewById(R.id.registerEmailConfirmTIET)
        registerConfirmEmailTIL = requireView().findViewById(R.id.registerEmailConfirmTIL)
        registerPasswordTIET = requireView().findViewById(R.id.registerPasswordTIET)
        registerPasswordTIL = requireView().findViewById(R.id.registerPasswordTIL)
        registerConfirmPasswordTIET = requireView().findViewById(R.id.registerPasswordConfirmTIET)
        registerConfirmPasswordTIL = requireView().findViewById(R.id.registerPasswordConfirmTIL)
        registrationButton = requireView().findViewById(R.id.registrationButton)
    }

    private fun checkEditTexts(): Boolean {
        return noEmptyEditTexts() && editTextConfirm()
    }

    private fun noEmptyEditTexts(): Boolean {
        if(!emptyEditText(firstNameTIL) &&
                !emptyEditText(lastNameTIL) &&
                !emptyEditText(registerEmailTIL) &&
                !emptyEditText(registerConfirmEmailTIL) &&
                !emptyEditText(registerPasswordTIL) &&
                !emptyEditText(registerConfirmPasswordTIL))
            return true
        return false
    }

    private fun emptyEditText(til: TextInputLayout): Boolean {
        if (til.editText?.text.toString().isEmpty()) {
            til.error = getString(R.string.empty_field)
            til.requestFocus()
            return true
        }
        return false
    }

    private fun editTextConfirm(): Boolean {
        if(registerEmailTIL.editText?.text.toString() != registerConfirmEmailTIL.editText?.text.toString()) {
            registerConfirmEmailTIL.error = getString(R.string.emails_dont_match)
            registerConfirmEmailTIL.requestFocus()
            return false
        }
        if(registerPasswordTIL.editText?.text.toString() != registerConfirmPasswordTIL.editText?.text.toString()) {
            registerConfirmPasswordTIL.error = getString(R.string.passwords_dont_match)
            registerConfirmPasswordTIL.requestFocus()
            return false
        }
        return true
    }

    private fun addTextChangeListener(til: TextInputLayout) {
        til.editText?.addTextChangedListener {
            til.error = ""
        }
    }

    private fun hideKeyboardFrom() {
        val imm: InputMethodManager = requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun render(viewState: RegisterViewState) {
        when(viewState) {
            DefaultState -> {
                registerProgressBar.visibility = View.INVISIBLE
            }
            Loading -> {
                registerProgressBar.visibility = View.VISIBLE
            }
            is Failure -> {
                if (viewState.exception is FirebaseAuthException) {
                    when (viewState.exception.errorCode) {
                        "ERROR_INVALID_EMAIL" -> {
                            registerEmailTIL.error = getString(R.string.bad_email_format)
                            registerEmailTIL.requestFocus()
                        }
                        "ERROR_EMAIL_ALREADY_IN_USE" -> {
                            registerEmailTIL.error = getString(R.string.email_already_exists)
                            registerEmailTIL.requestFocus()
                        }
                        "ERROR_WEAK_PASSWORD" -> {
                            registerPasswordTIL.error = getString(R.string.invalid_password)
                            registerPasswordTIL.requestFocus()
                        }
                        else -> SingleToast.show(requireContext(), viewState.exception.message, Toast.LENGTH_LONG)
                    }
                } else if (viewState.exception is FirebaseNetworkException) {
                    SingleToast.show(requireContext(), getString(R.string.network_error), Toast.LENGTH_LONG)
                } else {
                    SingleToast.show(requireContext(),viewState.exception.message, Toast.LENGTH_LONG)
                }
                viewModel.defaultState()
            }
            Success -> {
                hideKeyboardFrom()
                SingleToast.show(requireContext(), getString(R.string.successful_registration), Toast.LENGTH_LONG)
                navigator?.replace(MainFragment())
            }
        }.exhaustive
    }
}