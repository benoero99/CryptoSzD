package com.example.cryptoszd

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import co.zsmb.rainbowcake.navigation.SimpleNavActivity
import com.example.cryptoszd.ui.auth.login.LoginFragment
import com.example.cryptoszd.ui.main.MainFragment
import com.example.cryptoszd.ui.model.UiUser
import com.example.cryptoszd.utils.SingleToast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : SimpleNavActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null && Firebase.auth.currentUser == null) {
            navigator.add(LoginFragment())
        } else {
            navigator.add(MainFragment())
        }
    }

}