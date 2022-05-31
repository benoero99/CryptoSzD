package com.example.cryptoszd.domain.model

import java.security.cert.Certificate

data class DomainCertificate(
    val certificate: Certificate,
    val valid: Boolean,
    val expired: Boolean
)