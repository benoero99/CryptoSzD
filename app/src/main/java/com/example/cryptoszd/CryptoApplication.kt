package com.example.cryptoszd

import co.zsmb.rainbowcake.config.Loggers
import co.zsmb.rainbowcake.config.rainbowCake
import co.zsmb.rainbowcake.dagger.RainbowCakeApplication
import co.zsmb.rainbowcake.timber.TIMBER
import com.example.cryptoszd.di.AppComponent
import com.example.cryptoszd.di.DaggerAppComponent

class CryptoApplication : RainbowCakeApplication() {
    override lateinit var injector: AppComponent

    override fun setupInjector() {
        injector = DaggerAppComponent.create()
    }

    override fun onCreate() {
        super.onCreate()

        rainbowCake {
            isDebug = BuildConfig.DEBUG
            logger = Loggers.TIMBER
            consumeExecuteExceptions = false
        }
    }
}