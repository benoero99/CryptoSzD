package com.example.cryptoszd.network

object DbReference {
    object Collection {
        const val USERS =  "Users"
        const val SECRETKEY = "Secretkey"
        const val CERTIFICATE = "Certificate"
        const val PASSWORDS = "Passwords"
    }

    object Field {
        const val CERTIFICATEID = "certificateID"
        const val EMAIL = "email"
        const val USERNAME = "username"
        const val PUBLICKEY = "publicKey"
        const val PRIVATEKEY = "privateKey"
        const val CERTIFICATE = "certificate"
        const val COMMONNAME = "commonName"
        const val COUNTRYCODE = "countryCode"
        const val LOCALITY = "locality"
        const val CIPHEREX = "cipherEx"
        const val WEBSITE = "website"
        const val PASSWORD = "password"
    }
}