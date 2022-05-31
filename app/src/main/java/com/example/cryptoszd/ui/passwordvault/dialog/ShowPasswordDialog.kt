package com.example.cryptoszd.ui.passwordvault.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.example.cryptoszd.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ShowPasswordDialog(
    private val website: String,
    private val password: String,
    private val position: Int
    ): DialogFragment() {

    private lateinit var showWebsiteTIL: TextInputLayout
    private lateinit var showWebsiteTIET: TextInputEditText
    private lateinit var showPasswordTIL: TextInputLayout
    private lateinit var showPasswordTIET: TextInputEditText

    override fun onStart() {
        super.onStart()
        init()
    }

    private fun init() {
        showWebsiteTIL = dialog!!.findViewById(R.id.showWebsiteTIL)
        showWebsiteTIET = dialog!!.findViewById(R.id.showWebsiteTIET)
        showWebsiteTIET.setText(website)
        showPasswordTIL = dialog!!.findViewById(R.id.showPasswordTIL)
        showPasswordTIET = dialog!!.findViewById(R.id.showPasswordTIET)
        showPasswordTIET.setText(password)

        showWebsiteTIL.editText?.addTextChangedListener {
            showWebsiteTIL.error = ""
            showWebsiteTIL.isErrorEnabled = false
        }
        showPasswordTIL.editText?.addTextChangedListener {
            showPasswordTIL.error = ""
            showPasswordTIL.isErrorEnabled = false
        }
    }

    override fun onResume() {
        super.onResume()
        val d = dialog as AlertDialog?
        if (d != null) {
            val positiveButton: Button = d.getButton(Dialog.BUTTON_POSITIVE) as Button
            positiveButton.setOnClickListener(View.OnClickListener {
                if(checkEditText()) {
                    val listener = parentFragment as PasswordChangeListener
                    listener.passwordChanged(showWebsiteTIL.editText?.text.toString(),showPasswordTIL.editText?.text.toString(), position)
                    dialog!!.dismiss()
                }
            })
        }
    }

    private fun checkEditText(): Boolean {
        if(showWebsiteTIL.editText?.text.toString().isEmpty()) {
            showWebsiteTIL.error = context?.getString(R.string.empty_field)
            return false
        }
        if(showPasswordTIL.editText?.text.toString().isEmpty()) {
            showPasswordTIL.error = context?.getString(R.string.empty_field)
            return false
        }
        return true
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater;

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(inflater.inflate(R.layout.dialog_showpassword, null))
                // Add action buttons
                .setPositiveButton(getString(R.string.save), null)
                .setNegativeButton(getString(R.string.exit),null)
            builder.setTitle(getString(R.string.check_or_change_password))
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    interface PasswordChangeListener {
        fun passwordChanged(newWebsite: String, newPassword: String, position: Int)
    }
}