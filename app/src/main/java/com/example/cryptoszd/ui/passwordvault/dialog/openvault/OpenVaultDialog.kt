package com.example.cryptoszd.ui.passwordvault.dialog.openvault

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.widget.addTextChangedListener
import co.zsmb.rainbowcake.base.RainbowCakeDialogFragment
import co.zsmb.rainbowcake.dagger.getViewModelFromFactory
import co.zsmb.rainbowcake.extensions.exhaustive
import co.zsmb.rainbowcake.navigation.navigator
import com.example.cryptoszd.R
import com.example.cryptoszd.ui.passwordvault.passwordvault.PasswordVaultFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.wang.avi.AVLoadingIndicatorView

class OpenVaultDialog : RainbowCakeDialogFragment<OpenVaultViewState, OpenVaultViewModel>() {
    private lateinit var passwordTIL: TextInputLayout
    private lateinit var passwordTIET: TextInputEditText
    private lateinit var loadingProgressBar: AVLoadingIndicatorView

    override fun provideViewModel() = getViewModelFromFactory()
    override fun getViewResource() = R.layout.dialog_openvault

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
                viewModel.openVault(passwordTIL.editText?.text.toString().toCharArray())
            })
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            builder.setView(inflater.inflate(R.layout.dialog_openvault, null))
            builder.setPositiveButton(getString(R.string.open),
                null).setNegativeButton(getString(R.string.cancel), null)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun init() {
        passwordTIL = dialog!!.findViewById(R.id.masterPasswordOpenTIL)
        passwordTIET = dialog!!.findViewById(R.id.masterPasswordOpenTIET)
        loadingProgressBar = dialog!!.findViewById(R.id.openVaultLoading)

        passwordTIL.editText?.addTextChangedListener {
            passwordTIL.error = ""
            passwordTIL.isErrorEnabled = false
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        val parentFragment = parentFragment
        if (parentFragment is DialogInterface.OnDismissListener) {
            (parentFragment as DialogInterface.OnDismissListener?)!!.onDismiss(dialog)
        }
    }

    override fun render(viewState: OpenVaultViewState) {
        when(viewState) {
            Default -> {
                loadingProgressBar.hide()
            }
            is Error -> {
                loadingProgressBar.smoothToHide()
                passwordTIL.error = viewState.exception.message
            }
            Loading -> {
                loadingProgressBar.smoothToShow()
            }
            is Success -> {
                dialog!!.dismiss()
                navigator?.add(PasswordVaultFragment(viewState.passwords,passwordTIL.editText?.text.toString().toCharArray()))
            }
        }.exhaustive
    }
}