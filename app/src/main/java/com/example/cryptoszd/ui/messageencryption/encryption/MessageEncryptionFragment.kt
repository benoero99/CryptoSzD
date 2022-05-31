package com.example.cryptoszd.ui.messageencryption.encryption

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import co.zsmb.rainbowcake.base.RainbowCakeFragment
import co.zsmb.rainbowcake.dagger.getViewModelFromFactory
import co.zsmb.rainbowcake.extensions.exhaustive
import co.zsmb.rainbowcake.navigation.navigator
import com.example.cryptoszd.R
import com.example.cryptoszd.ui.messageencryption.listofcertificates.ListOfCertificatesFragment
import com.example.cryptoszd.utils.SingleToast
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.wang.avi.AVLoadingIndicatorView

class MessageEncryptionFragment: RainbowCakeFragment<MessageEncryptionViewState, MessageEncryptionViewModel>() {
    private lateinit var conversationPartnersNameTV: TextView
    private lateinit var conversationPartnersEmailTV: TextView
    private lateinit var errorTV: TextView
    private lateinit var pasteCertificateButton: MaterialButton
    private lateinit var searchCertificateButton: MaterialButton
    private lateinit var unencryptedTextTIL: TextInputLayout
    private lateinit var unencryptedTextTIET: TextInputEditText
    private lateinit var messageEncryptButton: MaterialButton
    private lateinit var messageDecryptButton: MaterialButton
    private lateinit var messageEncryptionProgressBar: AVLoadingIndicatorView
    private lateinit var encryptedTextTIL: TextInputLayout
    private lateinit var encryptedTextTIET: TextInputEditText

    private lateinit var pasteData: String
    private lateinit var certificateString: String

    @SuppressLint("MissingSuperCall")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()

        pasteCertificateButton.setOnClickListener {
            val clipboard: ClipboardManager = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

            // Paste from clipboard
            if (clipboard.hasPrimaryClip()) {
                val item = clipboard.primaryClip!!.getItemAt(0)
                pasteData = item.text.toString()
                errorTV.text = ""
                viewModel.getPastedCertificate(pasteData)
            } else {
               SingleToast.show(requireContext(), "There is nothing in clipboard", Toast.LENGTH_SHORT)
            }
        }
        searchCertificateButton.setOnClickListener {
            hideKeyboardFrom()
            navigator?.add(
                ListOfCertificatesFragment(),
                R.anim.right_to_left_slide_enter,
                R.anim.right_to_left_slide_exit,
                R.anim.left_to_right_slide_enter,
                R.anim.left_to_right_slide_exit
            )
        }
        messageEncryptButton.setOnClickListener {
            if(conversationPartnersEmailTV.text.toString().isEmpty()) {
                errorTV.text = getString(R.string.no_selected_certificate)
                return@setOnClickListener
            }
            viewModel.encryptMessage(certificateString, unencryptedTextTIL.editText?.text.toString())

        }
        messageDecryptButton.setOnClickListener {
            if(conversationPartnersEmailTV.text.toString().isEmpty()) {
                errorTV.text = getString(R.string.no_selected_certificate)
                return@setOnClickListener
            } else if(encryptedTextTIL.editText?.text.toString().isEmpty()) {
                errorTV.text = getString(R.string.nothing_to_decrypt)
                return@setOnClickListener
            }
            viewModel.decryptMessage(certificateString, encryptedTextTIL.editText?.text.toString())
        }
    }

    private fun init() {
        conversationPartnersNameTV = requireView().findViewById(R.id.conversationPartnersNameTV)
        conversationPartnersEmailTV = requireView().findViewById(R.id.conversationPartnersEmailTV)
        errorTV = requireView().findViewById(R.id.errorTV)
        pasteCertificateButton = requireView().findViewById(R.id.pasteCertificateButton)
        searchCertificateButton = requireView().findViewById(R.id.searchCertificateButton)
        unencryptedTextTIL = requireView().findViewById(R.id.unencryptedTextTIL)
        unencryptedTextTIET = requireView().findViewById(R.id.unencryptedTextTIET)
        messageEncryptButton = requireView().findViewById(R.id.messageEncryptButton)
        messageDecryptButton = requireView().findViewById(R.id.messageDecryptButton)
        messageEncryptionProgressBar = requireView().findViewById(R.id.messageEncryptionProgressBar)
        encryptedTextTIL = requireView().findViewById(R.id.encryptedTextTIL)
        encryptedTextTIET = requireView().findViewById(R.id.encryptedTextTIET)
    }

    override fun provideViewModel() = getViewModelFromFactory()
    override fun getViewResource() = R.layout.fragment_message_encryption

    private fun hideKeyboardFrom() {
        val imm: InputMethodManager = requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun render(viewState: MessageEncryptionViewState) {
        when(viewState) {
            Default -> {
                messageEncryptionProgressBar.smoothToHide()
            }
            Loading -> {
                messageEncryptionProgressBar.smoothToShow()
            }
            is Error -> {
                messageEncryptionProgressBar.smoothToHide()
                errorTV.text = viewState.exception.message
                when (viewState.exception.message) {
                    "Message decryption failed" -> {
                        SingleToast.show(requireContext(), getString(R.string.message_decryption_failed), Toast.LENGTH_SHORT)
                    }
                    "This message was not sent by the owner of the selected certificate!" -> {
                        SingleToast.show(requireContext(), getString(R.string.identical_error), Toast.LENGTH_SHORT)
                    }
                    "bad base-64" -> {
                        SingleToast.show(requireContext(), getString(R.string.wrong_encrypted_text), Toast.LENGTH_SHORT)
                    }
                    else -> {
                        SingleToast.show(requireContext(), viewState.exception.message, Toast.LENGTH_SHORT)
                    }
                }
                viewModel.default()
            }
            is SuccessfulPaste -> {
                messageEncryptionProgressBar.smoothToHide()
                conversationPartnersNameTV.text = viewState.commonName
                conversationPartnersEmailTV.text = viewState.email
                certificateString = pasteData
                viewModel.default()
            }
            is SuccessfulEncrypt -> {
                messageEncryptionProgressBar.smoothToHide()
                encryptedTextTIL.editText?.setText(viewState.encryptedMessage)
                viewModel.default()
            }
            is SuccessfulDecrypt -> {
                messageEncryptionProgressBar.smoothToHide()
                unencryptedTextTIL.editText?.setText(viewState.decryptedMessage)
                viewModel.default()
            }
        }.exhaustive
    }
}