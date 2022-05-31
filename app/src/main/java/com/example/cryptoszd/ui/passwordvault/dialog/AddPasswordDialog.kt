package com.example.cryptoszd.ui.passwordvault.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.example.cryptoszd.R
import com.example.cryptoszd.ui.model.UiPassword
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class AddPasswordDialog : DialogFragment() {
    private lateinit var addWebsiteTIL: TextInputLayout
    private lateinit var addWebsiteTIET: TextInputEditText
    private lateinit var addPasswordTIL: TextInputLayout
    private lateinit var addPasswordTIET: TextInputEditText


    override fun onStart() {
        super.onStart()
        init()
    }

    private fun init() {
        addWebsiteTIL = dialog!!.findViewById(R.id.addWebsiteTIL)
        addWebsiteTIET = dialog!!.findViewById(R.id.addWebsiteTIET)
        addPasswordTIL = dialog!!.findViewById(R.id.addPasswordTIL)
        addPasswordTIET = dialog!!.findViewById(R.id.addPasswordTIET)

        addWebsiteTIL.editText?.addTextChangedListener {
            addWebsiteTIL.error = ""
            addWebsiteTIL.isErrorEnabled = false
        }
        addPasswordTIL.editText?.addTextChangedListener {
            addPasswordTIL.error = ""
            addPasswordTIL.isErrorEnabled = false
        }
    }

    override fun onResume() {
        super.onResume()
        val d = dialog as AlertDialog?
        if (d != null) {
            val positiveButton: Button = d.getButton(Dialog.BUTTON_POSITIVE) as Button
            positiveButton.setOnClickListener(View.OnClickListener {
                if(checkEditTexts()) {
                    val mHost = parentFragment as PassData
                    mHost.passData(UiPassword(
                        website = addWebsiteTIL.editText?.text.toString(),
                        password = addPasswordTIL.editText?.text.toString()
                    ))
                    dialog!!.dismiss()
                }
            })
        }
    }

    private fun checkEditTexts(): Boolean {
        if(addWebsiteTIL.editText?.text.toString().isEmpty()) {
            addWebsiteTIL.error = getString(R.string.empty_field)
            addWebsiteTIL.requestFocus()
            return false
        }
        if(addPasswordTIL.editText?.text.toString().isEmpty()) {
            addPasswordTIL.error = getString(R.string.empty_field)
            addPasswordTIL.requestFocus()
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
            builder.setView(inflater.inflate(R.layout.dialog_addpassword, null))
                // Add action buttons
                .setPositiveButton(getString(R.string.add), null)
                .setNegativeButton(getString(R.string.cancel),null)
            builder.setTitle(getString(R.string.add_new_password))
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    interface PassData {
        fun passData(data: UiPassword)
    }
}