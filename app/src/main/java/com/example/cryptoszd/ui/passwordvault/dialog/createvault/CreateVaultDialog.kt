package com.example.cryptoszd.ui.passwordvault.dialog.createvault

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import co.zsmb.rainbowcake.base.RainbowCakeDialogFragment
import co.zsmb.rainbowcake.dagger.getViewModelFromFactory
import co.zsmb.rainbowcake.extensions.exhaustive
import com.example.cryptoszd.R
import com.example.cryptoszd.utils.SingleToast
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.wang.avi.AVLoadingIndicatorView

class CreateVaultDialog : RainbowCakeDialogFragment<CreateVaultViewState, CreateVaultViewModel>() {
    private lateinit var passwordTIL: TextInputLayout
    private lateinit var passwordTIET: TextInputEditText
    private lateinit var confirmPasswordTIL: TextInputLayout
    private lateinit var confirmPasswordTIET: TextInputEditText
    private lateinit var loadingProgressBar: AVLoadingIndicatorView
    private lateinit var mHost: VaultCreation


    override fun provideViewModel() = getViewModelFromFactory()
    override fun getViewResource() = R.layout.dialog_createvault

    override fun onStart() {
        super.onStart()
        init()
    }

    override fun onResume() {
        super.onResume()
        val d = dialog as AlertDialog?
        if (d != null) {
            val positiveButton: Button = d.getButton(Dialog.BUTTON_POSITIVE) as Button
            positiveButton.setOnClickListener(View.OnClickListener {
                if(checkEditTexts()) {
                    viewModel.createVault(passwordTIL.editText?.text.toString().toCharArray())
                    mHost.passData(true)
                }
            })
        }
    }

    private fun init() {
        mHost = parentFragment as VaultCreation
        passwordTIL = dialog!!.findViewById(R.id.masterPasswordTIL)
        passwordTIET = dialog!!.findViewById(R.id.masterPasswordTIET)
        confirmPasswordTIL = dialog!!.findViewById(R.id.confirmMasterPasswordTIL)
        confirmPasswordTIET = dialog!!.findViewById(R.id.confirmMasterPasswordTIET)
        loadingProgressBar = dialog!!.findViewById(R.id.createVaultLoading)

        passwordTIL.editText?.addTextChangedListener {
            passwordTIL.error = ""
            passwordTIL.isErrorEnabled = false
        }
        confirmPasswordTIL.editText?.addTextChangedListener {
            confirmPasswordTIL.error = ""
            confirmPasswordTIL.isErrorEnabled = false
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            builder.setView(inflater.inflate(R.layout.dialog_createvault, null))
            builder.setPositiveButton(getString(R.string.create),
                null).setNegativeButton(getString(R.string.cancel), null)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun checkEditTexts(): Boolean {
        if(passwordTIL.editText?.text.toString().isEmpty()) {
            passwordTIL.error = getString(R.string.empty_password_error)
            passwordTIL.requestFocus()
            return false
        }
        if(passwordTIL.editText?.text.toString().length < 10) {
            passwordTIL.error = getString(R.string.password_min_10)
            passwordTIL.requestFocus()
            return false
        }
        if(confirmPasswordTIL.editText?.text.toString() != passwordTIL.editText?.text.toString()) {
            confirmPasswordTIL.error = getString(R.string.passwords_dont_match)
            confirmPasswordTIL.requestFocus()
            return false
        }
        return true
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        val parentFragment = parentFragment
        if (parentFragment is DialogInterface.OnDismissListener) {
            (parentFragment as DialogInterface.OnDismissListener?)!!.onDismiss(dialog)
        }
    }

    interface VaultCreation {
        fun passData(data: Boolean)
    }

    override fun render(viewState: CreateVaultViewState) {
        when(viewState) {
            Default -> {
                loadingProgressBar.hide()
            }
            is Error -> {
                loadingProgressBar.smoothToHide()
                SingleToast.show(requireContext(),viewState.exception.message, Toast.LENGTH_SHORT)
            }
            Loading -> {
                loadingProgressBar.smoothToShow()
            }
            Success -> {
                dialog!!.dismiss()
            }
        }.exhaustive
    }

}