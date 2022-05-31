package com.example.cryptoszd.di

import androidx.lifecycle.ViewModel
import co.zsmb.rainbowcake.dagger.ViewModelKey
import com.example.cryptoszd.ui.messageencryption.createcertificate.CreateCertificateViewModel
import com.example.cryptoszd.ui.passwordvault.dialog.createvault.CreateVaultViewModel
import com.example.cryptoszd.ui.passwordvault.dialog.openvault.OpenVaultViewModel
import com.example.cryptoszd.ui.fileencryption.FileEncryptionViewModel
import com.example.cryptoszd.ui.messageencryption.listofcertificates.ListOfCertificatesViewModel
import com.example.cryptoszd.ui.auth.login.LoginViewModel
import com.example.cryptoszd.ui.main.MainViewModel
import com.example.cryptoszd.ui.passwordvault.passwordvault.PasswordVaultViewModel
import com.example.cryptoszd.ui.auth.register.RegisterViewModel
import com.example.cryptoszd.ui.messageencryption.certificate.CertificateViewModel
import com.example.cryptoszd.ui.messageencryption.encryption.MessageEncryptionViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    abstract fun bindLoginViewModel(loginViewModel: LoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RegisterViewModel::class)
    abstract fun bindRegisterViewModel(registerViewModel: RegisterViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(mainViewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CreateCertificateViewModel::class)
    abstract fun bindCreateCertificateViewModel(createCertificateViewModel: CreateCertificateViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ListOfCertificatesViewModel::class)
    abstract fun bindListOfCertificatesViewModel(listOfCertificatesViewModel: ListOfCertificatesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CreateVaultViewModel::class)
    abstract fun bindCreateVaultViewModel(createVaultViewModel: CreateVaultViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OpenVaultViewModel::class)
    abstract fun bindOpenVaultViewModel(openVaultViewModel: OpenVaultViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PasswordVaultViewModel::class)
    abstract fun bindPasswordVaultViewModel(passwordVaultViewModel: PasswordVaultViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FileEncryptionViewModel::class)
    abstract fun bindFileEncryptionViewModel(fileEncryptionViewModel: FileEncryptionViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CertificateViewModel::class)
    abstract fun bindCertificateViewModel(certificateViewModel: CertificateViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MessageEncryptionViewModel::class)
    abstract fun bindMessageEncryptionViewModel(messageEncryptionViewModel:MessageEncryptionViewModel): ViewModel
}
