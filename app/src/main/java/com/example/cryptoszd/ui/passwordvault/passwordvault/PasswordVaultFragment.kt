package com.example.cryptoszd.ui.passwordvault.passwordvault

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.zsmb.rainbowcake.base.RainbowCakeFragment
import co.zsmb.rainbowcake.dagger.getViewModelFromFactory
import co.zsmb.rainbowcake.extensions.exhaustive
import com.example.cryptoszd.R
import com.example.cryptoszd.utils.SingleToast
import com.example.cryptoszd.adapter.PasswordAdapter
import com.example.cryptoszd.ui.passwordvault.dialog.AddPasswordDialog
import com.example.cryptoszd.ui.passwordvault.dialog.ShowPasswordDialog
import com.example.cryptoszd.ui.model.UiPassword
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.wang.avi.AVLoadingIndicatorView

class PasswordVaultFragment(
    private var passwords: MutableList<UiPassword>,
    private val password: CharArray
) : RainbowCakeFragment<PasswordVaultViewState, PasswordVaultViewModel>(),
    AddPasswordDialog.PassData,
    PasswordAdapter.PasswordItemClickListener,
    ShowPasswordDialog.PasswordChangeListener
{
    private lateinit var passwordsRV: RecyclerView
    private lateinit var passwordFab: FloatingActionButton
    private lateinit var passwordVaultLoading: AVLoadingIndicatorView
    private lateinit var saveButton: MaterialButton
    private val adapter = PasswordAdapter(this)

    override fun provideViewModel() = getViewModelFromFactory()
    override fun getViewResource() = R.layout.fragment_passwordvault

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        passwordFab = view.findViewById(R.id.passwordVaultFab)
        passwordsRV = view.findViewById(R.id.passwordsRV)
        passwordVaultLoading = view.findViewById(R.id.passwordVaultProgressBar)
        saveButton = view.findViewById(R.id.saveButton)


        passwordsRV.layoutManager = LinearLayoutManager(requireContext())
        adapter.swapData(passwords)
        passwordsRV.adapter = adapter

        passwordFab.setOnClickListener {
            val dialog = AddPasswordDialog()
            dialog.show(childFragmentManager, "")
        }
        saveButton.setOnClickListener {
            viewModel.savePasswords(adapter.data, password)
        }
    }

    override fun render(viewState: PasswordVaultViewState) {
        when(viewState) {
            Default -> {

            }
            is Error -> {
                passwordVaultLoading.smoothToHide()
                SingleToast.show(requireContext(), viewState.exception.message, Toast.LENGTH_SHORT)
            }
            Loading -> {
                passwordVaultLoading.smoothToShow()
            }
            Success -> {
                passwordVaultLoading.smoothToHide()
                SingleToast.show(requireContext(), getString(R.string.chenges_saved), Toast.LENGTH_SHORT)
            }
        }.exhaustive
    }

    override fun passData(data: UiPassword) {
        adapter.addItem(data)
    }

    override fun itemClicked(website :String, password: String,position: Int) {
        val dialog = ShowPasswordDialog(website, password, position)
        dialog.show(childFragmentManager, "")
    }

    override fun passwordChanged(newWebsite: String, newPassword: String, position: Int) {
        adapter.updateItem(newWebsite,newPassword,position)
    }
}