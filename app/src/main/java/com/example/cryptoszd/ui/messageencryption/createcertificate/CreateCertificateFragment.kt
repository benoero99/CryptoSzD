package com.example.cryptoszd.ui.messageencryption.createcertificate

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import co.zsmb.rainbowcake.base.RainbowCakeFragment
import co.zsmb.rainbowcake.dagger.getViewModelFromFactory
import co.zsmb.rainbowcake.extensions.exhaustive
import co.zsmb.rainbowcake.navigation.navigator
import com.example.cryptoszd.R
import com.example.cryptoszd.utils.SingleToast
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.toptoche.searchablespinnerlibrary.SearchableSpinner
import com.wang.avi.AVLoadingIndicatorView


class CreateCertificateFragment(private var commonName: String, private var email: String) : RainbowCakeFragment<CertificateViewState, CreateCertificateViewModel>() {
    private lateinit var countryCodeSS: SearchableSpinner
    private lateinit var cnTIL: TextInputLayout
    private lateinit var cnTIET: TextInputEditText
    private lateinit var certificateEmailTIL: TextInputLayout
    private lateinit var certificateEmailTIET: TextInputEditText
    private lateinit var locationTIL: TextInputLayout
    private lateinit var locationTIET: TextInputEditText
    private lateinit var progressBar: AVLoadingIndicatorView
    private lateinit var certificateButton: MaterialButton
    private lateinit var ssErrorTV: TextView

    override fun provideViewModel() = getViewModelFromFactory()
    override fun getViewResource() = R.layout.fragment_create_certificate

    @SuppressLint("ClickableViewAccessibility", "MissingSuperCall")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        var mLastClickTime: Long = 0
        countryCodeSS.setOnTouchListener { v, event ->
            ssErrorTV.visibility = View.GONE
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnTouchListener false
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            event.setAction(MotionEvent.ACTION_UP)
            countryCodeSS.onTouch(v, event)
            false
        }
        cnTIL.editText?.addTextChangedListener {
            cnTIL.error = ""
            cnTIL.isErrorEnabled = false
        }
        certificateEmailTIL.editText?.addTextChangedListener {
            certificateEmailTIL.error = ""
            certificateEmailTIL.isErrorEnabled = false
        }
        locationTIL.editText?.addTextChangedListener {
            locationTIL.error = ""
            locationTIL.isErrorEnabled = false
        }
        certificateButton.setOnClickListener {
            if (emptyFieldCheck()) {
                val location = locationTIET.text.toString()
                val countryAndCountryCode = countryCodeSS.selectedItem.toString()
                val stringList = countryAndCountryCode.split(" ").toMutableList()
                val stringListWithoutLast = stringList.dropLast(1)
                val countryCode = stringListWithoutLast.last()
                commonName = cnTIET.text.toString()
                email = certificateEmailTIET.text.toString()
                viewModel.createCertificate(commonName, countryCode, location, email)
            }
        }
    }

    private fun init() {
        countryCodeSS = requireView().findViewById(R.id.countryCodeSS)
        countryCodeSS.setTitle(getString(R.string.select_country_code))
        countryCodeSS.setPositiveButton("OK")
        val spinnerAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.country_data,
            android.R.layout.simple_spinner_item
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countryCodeSS.adapter = spinnerAdapter
        progressBar = requireView().findViewById(R.id.certificateProgressBar)
        cnTIL = requireView().findViewById(R.id.cnTIL)
        cnTIL.editText?.setText(commonName)
        cnTIET = requireView().findViewById(R.id.cnTIET)
        certificateEmailTIL = requireView().findViewById(R.id.certificateEmailTIL)
        certificateEmailTIL.editText?.setText(email)
        certificateEmailTIET = requireView().findViewById(R.id.certificateEmailTIET)
        locationTIL = requireView().findViewById(R.id.locationTIL)
        locationTIET = requireView().findViewById(R.id.locationTIET)
        certificateButton = requireView().findViewById(R.id.certificateButton)
        ssErrorTV = requireView().findViewById(R.id.ssErrorTV)
    }

    private fun emptyFieldCheck(): Boolean {
        if (cnTIET.text.toString().isEmpty()) {
            cnTIL.error = getString(R.string.empty_field)
            cnTIET.requestFocus()
            return false
        }
        if (certificateEmailTIET.text.toString().isEmpty()) {
            certificateEmailTIL.error = getString(R.string.empty_field)
            certificateEmailTIET.requestFocus()
            return false
        }
        if (locationTIET.text.toString().isEmpty()) {
            locationTIL.error = getString(R.string.empty_field)
            locationTIET.requestFocus()
            return false
        }
        if (countryCodeSS.selectedItemPosition == -1) {
            ssErrorTV.visibility = View.VISIBLE
            return false
        }
        return true
    }

    override fun render(viewState: CertificateViewState) {
        when(viewState) {
            Default -> {
                progressBar.smoothToHide()
            }
            Loading -> {
                progressBar.smoothToShow()
            }
            is Failed -> {

                when (viewState.exception.message) {
                    "Network Error" -> {
                        SingleToast.show(requireContext(), getString(R.string.network_error), Toast.LENGTH_SHORT)
                    }
                    else -> {
                        SingleToast.show(requireContext(), viewState.exception.message, Toast.LENGTH_SHORT)
                    }
                }
                viewModel.default()
            }
            Success -> {
                navigator?.pop()
                SingleToast.show(requireContext(), getString(R.string.certificate_created), Toast.LENGTH_SHORT)
            }
        }.exhaustive
    }
}

