package com.example.cryptoszd.ui.messageencryption.listofcertificates

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.zsmb.rainbowcake.base.RainbowCakeFragment
import co.zsmb.rainbowcake.dagger.getViewModelFromFactory
import co.zsmb.rainbowcake.extensions.exhaustive
import co.zsmb.rainbowcake.navigation.navigator
import com.example.cryptoszd.R
import com.example.cryptoszd.adapter.CertificateAdapter
import com.example.cryptoszd.ui.messageencryption.certificate.CertificateFragment
import com.example.cryptoszd.ui.model.UiCertificateList
import com.example.cryptoszd.utils.SingleToast
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.wang.avi.AVLoadingIndicatorView
import java.util.*


class ListOfCertificatesFragment : RainbowCakeFragment<ListOfCertificatesViewState, ListOfCertificatesViewModel>(), CertificateAdapter.CertificateItemClickListener {
    private lateinit var certificateListProgressBar: AVLoadingIndicatorView
    private lateinit var recyclerView: RecyclerView
    private lateinit var certificateListSearcherTIL: TextInputLayout
    private lateinit var certificateListSearcherTIET: TextInputEditText
    private lateinit var list: MutableList<UiCertificateList>
    private val adapter = CertificateAdapter(this)


    override fun provideViewModel() = getViewModelFromFactory()
    override fun getViewResource() = R.layout.fragment_listofcertificates



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        certificateListSearcherTIL = view.findViewById(R.id.certificateListSearcherTIL)
        certificateListSearcherTIET = view.findViewById(R.id.certificateListSearcherTIET)

        certificateListProgressBar = view.findViewById(R.id.certificateListProgressBar)
        recyclerView = view.findViewById(R.id.certificateRV)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        viewModel.loadCertificates()
        certificateListSearcherTIL.editText?.addTextChangedListener {
            filter(certificateListSearcherTIL.editText?.text.toString())
        }

    }

    private fun filter(text: String) {
        val noCaseSensitiveText = text.toUpperCase(Locale.ROOT)
        val tempList = mutableListOf<UiCertificateList>()
        for (certificate in list) {
            if(certificate.commonName.toUpperCase(Locale.ROOT).contains(noCaseSensitiveText) ||
                certificate.email.toUpperCase(Locale.ROOT).contains(noCaseSensitiveText) ||
                certificate.locality.toUpperCase(Locale.ROOT).contains(noCaseSensitiveText) ||
                certificate.countryCode.toUpperCase(Locale.ROOT).contains(noCaseSensitiveText)
            ) {
                tempList.add(certificate)
            }
        }
        adapter.swapData(tempList)
    }

    override fun itemClicked(certificateID: String) {
        navigator?.add(
            CertificateFragment(certificateID),
            R.anim.right_to_left_slide_enter,
            R.anim.right_to_left_slide_exit,
            R.anim.left_to_right_slide_enter,
            R.anim.left_to_right_slide_exit
        )
    }

    override fun render(viewState: ListOfCertificatesViewState) {
        when (viewState) {
            Init -> {

            }
            Default -> {
                certificateListProgressBar.smoothToHide()
            }
            is Loaded -> {
                certificateListProgressBar.smoothToHide()
                adapter.swapData(viewState.list)
                list = viewState.list
                filter(certificateListSearcherTIL.editText?.text.toString())
                recyclerView.adapter = adapter
            }
            is Error -> {
                certificateListProgressBar.smoothToHide()
                SingleToast.show(requireContext(), viewState.exception.message, Toast.LENGTH_SHORT)
            }
        }.exhaustive
    }

}