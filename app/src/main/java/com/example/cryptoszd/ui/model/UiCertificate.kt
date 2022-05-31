package com.example.cryptoszd.ui.model

data class UiCertificate(
    val certificate: String,
    val valid: Boolean,
    val expired: Boolean,
    val subjectCommonName: String,
    val subjectCountry: String,
    val subjectEmail: String,
    val subjectLocality: String,
    val issuerCommonName: String,
    val issuerCountry: String,
    val issuerLocality: String,
    val issuerOrganization: String,
    val publicKey: String,
    val signatureAlgorithm: String,
    val signature: String,
    val validNotBefore: String,
    val validNotAfter: String,
    val serialNumber: String,
    val version: String,
    val type: String
    )