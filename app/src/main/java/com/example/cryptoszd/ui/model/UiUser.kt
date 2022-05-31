package com.example.cryptoszd.ui.model

data class UiUser(
    val username: String,
    val email: String,
    val certificateID: String?,
    var hasPasswordVault: Boolean
)