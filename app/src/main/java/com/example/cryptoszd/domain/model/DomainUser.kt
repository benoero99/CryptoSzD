package com.example.cryptoszd.domain.model

data class DomainUser(
    val username: String,
    val email: String,
    val certificateID: String?,
    val cipherEx: String?
)
