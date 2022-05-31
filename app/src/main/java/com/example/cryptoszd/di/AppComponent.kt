package com.example.cryptoszd.di

import co.zsmb.rainbowcake.dagger.RainbowCakeComponent
import co.zsmb.rainbowcake.dagger.RainbowCakeModule
import dagger.Component
import javax.inject.Singleton

//minden modul, amit tud Provide-olni
@Singleton
@Component(
    modules = [
        RainbowCakeModule::class,
        ViewModelModule::class,
        ApplicationModule::class,
        FirebaseModule::class,
        DbReferenceModule::class
    ]
)
interface AppComponent : RainbowCakeComponent
