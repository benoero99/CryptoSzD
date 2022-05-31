package com.example.cryptoszd.ui.messageencryption.certificate

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.*
import co.zsmb.rainbowcake.base.RainbowCakeFragment
import co.zsmb.rainbowcake.dagger.getViewModelFromFactory
import co.zsmb.rainbowcake.extensions.exhaustive
import co.zsmb.rainbowcake.navigation.navigator
import com.example.cryptoszd.R
import com.example.cryptoszd.ui.messageencryption.createcertificate.CreateCertificateFragment
import com.example.cryptoszd.utils.SingleToast
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.wang.avi.AVLoadingIndicatorView

class CertificateFragment(private val certificateID: String): RainbowCakeFragment<CertificateViewState, CertificateViewModel>() {
    private lateinit var certificateUsernameTV: TextView
    private lateinit var certificateValidityTV: TextView
    private lateinit var certificateValidityIV: ImageView
    private lateinit var renewCertificateButton: MaterialButton
    private lateinit var subjectCommonNameTV: TextView
    private lateinit var subjectCountryTV: TextView
    private lateinit var subjectEmailTV: TextView
    private lateinit var subjectLocalityTV: TextView
    private lateinit var issuerCommonNameTV: TextView
    private lateinit var issuerCountryTV: TextView
    private lateinit var issuerLocalityTV: TextView
    private lateinit var issuerOrganizationTV: TextView
    private lateinit var publicKeyTV: TextView
    private lateinit var signatureAlgorithmTV: TextView
    private lateinit var signatureTV: TextView
    private lateinit var validNotBeforeTV: TextView
    private lateinit var validNotAfterTV: TextView
    private lateinit var serialNumberTV: TextView
    private lateinit var versionTV: TextView
    private lateinit var typeTV: TextView
    private lateinit var certificateProgressBar: AVLoadingIndicatorView

    private lateinit var certificate: String

    override fun provideViewModel() = getViewModelFromFactory()
    override fun getViewResource() = R.layout.fragment_certificate

    private lateinit var certificateView: LinearLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getCertificate(certificateID)
        init()

        renewCertificateButton.setOnClickListener {
            navigator?.replace(CreateCertificateFragment(subjectCommonNameTV.text.toString(),subjectEmailTV.text.toString()),
                R.anim.right_to_left_slide_enter,
                R.anim.right_to_left_slide_exit,
                R.anim.left_to_right_slide_enter,
                R.anim.left_to_right_slide_exit
            )
        }

        certificateView.setOnLongClickListener {
            val clipboard: ClipboardManager = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("public key", certificate)
            clipboard.setPrimaryClip(clip)
            SingleToast.show(requireContext(),getString(R.string.certificate_copied), Toast.LENGTH_SHORT)
            return@setOnLongClickListener true
        }
    }

    private fun init() {
        certificateUsernameTV = requireView().findViewById(R.id.certificateUsernameTV)
        certificateValidityTV = requireView().findViewById(R.id.certificateValidityTV)
        certificateValidityIV = requireView().findViewById(R.id.certificateValidityIV)
        renewCertificateButton = requireView().findViewById(R.id.renewCertificateButton)
        subjectCommonNameTV = requireView().findViewById(R.id.subjectCommonNameTV)
        subjectCountryTV = requireView().findViewById(R.id.subjectCountryTV)
        subjectEmailTV = requireView().findViewById(R.id.subjectEmailTV)
        subjectLocalityTV = requireView().findViewById(R.id.subjectLocalityTV)
        issuerCommonNameTV = requireView().findViewById(R.id.issuerCommonNameTV)
        issuerCountryTV = requireView().findViewById(R.id.issuerCountryTV)
        issuerLocalityTV = requireView().findViewById(R.id.issuerLocalityTV)
        issuerOrganizationTV = requireView().findViewById(R.id.issuerOrganizationTV)
        publicKeyTV = requireView().findViewById(R.id.publicKeyTV)
        signatureAlgorithmTV = requireView().findViewById(R.id.signatureAlgorithmTV)
        signatureTV = requireView().findViewById(R.id.signatureTV)
        validNotBeforeTV = requireView().findViewById(R.id.validNotBeforeTV)
        validNotAfterTV = requireView().findViewById(R.id.validNotAfterTV)
        serialNumberTV = requireView().findViewById(R.id.serialNumberTV)
        versionTV = requireView().findViewById(R.id.versionTV)
        typeTV = requireView().findViewById(R.id.typeTV)
        certificateProgressBar = requireView().findViewById(R.id.certificateProgressBar)

        certificateView = requireView().findViewById(R.id.certificateView)

    }

    override fun render(viewState: CertificateViewState) {
        when(viewState) {
            is Error -> {
                SingleToast.show(requireContext(), viewState.exception.message, Toast.LENGTH_SHORT)
                view?.findViewById<LinearLayout>(R.id.certificateLoadingLayout)?.visibility = View.GONE
            }
            Init -> {
                view?.findViewById<LinearLayout>(R.id.certificateLoadingLayout)?.visibility = View.VISIBLE
            }
            is Loaded -> {
                view?.findViewById<LinearLayout>(R.id.certificateLoadingLayout)?.visibility = View.GONE
                certificateProgressBar.smoothToHide()

                certificate = viewState.uiCertificate.certificate

                certificateUsernameTV.text = viewState.uiCertificate.subjectCommonName
                if(!viewState.uiCertificate.valid) {
                    certificateValidityTV.text = getString(R.string.invalid_certificate)
                    certificateValidityIV.setImageResource(R.mipmap.ic_redx)
                } else if(viewState.uiCertificate.expired) {
                    certificateValidityTV.text = getString(R.string.expired_certificate)
                    certificateValidityIV.setImageResource(R.mipmap.ic_redx)
                    if(viewState.uiCertificate.subjectEmail == Firebase.auth.currentUser!!.email) {
                        renewCertificateButton.visibility = View.VISIBLE
                    }
                } else {
                    certificateValidityTV.text = getString(R.string.valid_certificate)
                    certificateValidityIV.setImageResource(R.mipmap.ic_checked)
                }

                subjectCommonNameTV.text = viewState.uiCertificate.subjectCommonName
                subjectCountryTV.text = viewState.uiCertificate.subjectCountry
                subjectEmailTV.text = viewState.uiCertificate.subjectEmail
                subjectLocalityTV.text = viewState.uiCertificate.subjectLocality
                issuerCommonNameTV.text = viewState.uiCertificate.issuerCommonName
                issuerCountryTV.text = viewState.uiCertificate.issuerCountry
                issuerLocalityTV.text = viewState.uiCertificate.issuerLocality
                issuerOrganizationTV.text = viewState.uiCertificate.issuerOrganization
                publicKeyTV.text = viewState.uiCertificate.publicKey
                signatureAlgorithmTV.text = viewState.uiCertificate.signatureAlgorithm
                signatureTV.text = viewState.uiCertificate.signature
                validNotBeforeTV.text = viewState.uiCertificate.validNotBefore
                validNotAfterTV.text = viewState.uiCertificate.validNotAfter
                serialNumberTV.text = viewState.uiCertificate.serialNumber
                versionTV.text = viewState.uiCertificate.version
                typeTV.text = viewState.uiCertificate.type
            }
        }.exhaustive
    }
}