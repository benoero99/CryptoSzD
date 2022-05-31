package com.example.cryptoszd.ui.main

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import co.zsmb.rainbowcake.base.RainbowCakeFragment
import co.zsmb.rainbowcake.dagger.getViewModelFromFactory
import co.zsmb.rainbowcake.extensions.exhaustive
import co.zsmb.rainbowcake.navigation.navigator
import com.example.cryptoszd.R
import com.example.cryptoszd.utils.SingleToast
import com.example.cryptoszd.ui.passwordvault.dialog.createvault.CreateVaultDialog
import com.example.cryptoszd.ui.messageencryption.createcertificate.CreateCertificateFragment
import com.example.cryptoszd.ui.passwordvault.dialog.openvault.OpenVaultDialog
import com.example.cryptoszd.ui.fileencryption.FileEncryptionFragment
import com.example.cryptoszd.ui.messageencryption.listofcertificates.ListOfCertificatesFragment
import com.example.cryptoszd.ui.auth.login.LoginFragment
import com.example.cryptoszd.ui.messageencryption.certificate.CertificateFragment
import com.example.cryptoszd.ui.messageencryption.encryption.MessageEncryptionFragment
import com.example.cryptoszd.ui.model.UiUser
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.wang.avi.AVLoadingIndicatorView

class MainFragment : RainbowCakeFragment<MainViewState, MainViewModel>(), DialogInterface.OnDismissListener,
    CreateVaultDialog.VaultCreation {
    private lateinit var welcomeTV: TextView
    private lateinit var certificateButton: MaterialButton
    private lateinit var listCertificatesButton: MaterialButton
    private lateinit var passwordVaultButton: MaterialButton
    private lateinit var fileEncryptionButton: MaterialButton
    private lateinit var messageEncryptionButton: MaterialButton
    private lateinit var logoutButton: MaterialButton
    private lateinit var loadingView: AVLoadingIndicatorView
    private lateinit var user: UiUser

    override fun provideViewModel() = getViewModelFromFactory()
    override fun getViewResource() = R.layout.fragment_main

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        viewModel.init()
        certificateButton.setOnClickListener {
            if(user.certificateID == null) {
                navigator?.add(CreateCertificateFragment(user.username, user.email),
                    R.anim.right_to_left_slide_enter,
                    R.anim.right_to_left_slide_exit,
                    R.anim.left_to_right_slide_enter,
                    R.anim.left_to_right_slide_exit
                )
            } else {
                navigator?.add(CertificateFragment(user.certificateID!!),
                    R.anim.right_to_left_slide_enter,
                    R.anim.right_to_left_slide_exit,
                    R.anim.left_to_right_slide_enter,
                    R.anim.left_to_right_slide_exit
                )
            }
        }
        listCertificatesButton.setOnClickListener {
            navigator?.add(ListOfCertificatesFragment(),
                R.anim.right_to_left_slide_enter,
                R.anim.right_to_left_slide_exit,
                R.anim.left_to_right_slide_enter,
                R.anim.left_to_right_slide_exit
            )
        }
        passwordVaultButton.setOnClickListener {
            viewModel.userHasPasswordVault(user)
        }
        fileEncryptionButton.setOnClickListener {
            navigator?.add(FileEncryptionFragment())
        }
        messageEncryptionButton.setOnClickListener {
            navigator?.add(MessageEncryptionFragment())
        }
        logoutButton.setOnClickListener {
            Firebase.auth.signOut()
            navigator?.replace(LoginFragment())
        }

    }


    private fun init() {
        welcomeTV = requireView().findViewById(R.id.welcomeTV)
        certificateButton = requireView().findViewById(R.id.manageCertificateButton)
        listCertificatesButton = requireView().findViewById(R.id.listCertificatesButton)
        passwordVaultButton = requireView().findViewById(R.id.passwordVaultButton)
        fileEncryptionButton = requireView().findViewById(R.id.fileEncryptionButton)
        messageEncryptionButton = requireView().findViewById(R.id.messageEncryptionButton)
        logoutButton = requireView().findViewById(R.id.logoutButton)
        loadingView = requireView().findViewById(R.id.loadingView)
    }

    override fun onDismiss(dialog: DialogInterface?) {
        viewModel.default(user)
    }

    override fun render(viewState: MainViewState) {
        when (viewState) {
            Init -> {
                view?.findViewById<LinearLayout>(R.id.mainLoadingLayout)?.visibility = View.VISIBLE
            }
            is Loaded -> {
                user = viewState.user
                welcomeTV.text = getString(R.string.welcome_to_cryptoszd, user.username)
                view?.findViewById<LinearLayout>(R.id.mainLoadingLayout)?.visibility = View.GONE
                loadingView.smoothToHide()
            }
            Loading -> {
                loadingView.smoothToShow()
            }
            is Error -> {
                SingleToast.show(requireContext(), viewState.exception.message.toString(), Toast.LENGTH_SHORT)
                viewModel.default(user)
            }
            CreateMasterPassword -> {
                val dialog = CreateVaultDialog()
                dialog.show(childFragmentManager, "")
            }
            GiveMasterPassword -> {
                val dialog = OpenVaultDialog()
                dialog.show(childFragmentManager, "")
            }
        }.exhaustive
    }

    override fun passData(data: Boolean) {
        user.hasPasswordVault = data
    }


}