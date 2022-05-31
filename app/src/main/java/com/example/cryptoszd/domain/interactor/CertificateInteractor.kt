package com.example.cryptoszd.domain.interactor

import android.util.Base64
import android.util.Base64.encodeToString
import com.example.cryptomod.BenoCrypto
import com.example.cryptoszd.domain.model.DomainCertificate
import com.example.cryptoszd.network.NetworkDataSource
import com.example.cryptoszd.ui.model.UiCertificateList
import com.google.android.gms.common.util.IOUtils
import org.spongycastle.asn1.x500.RDN
import org.spongycastle.asn1.x500.X500Name
import org.spongycastle.asn1.x500.style.BCStyle
import org.spongycastle.cert.jcajce.JcaX509CertificateHolder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.security.cert.Certificate
import java.security.cert.X509Certificate
import javax.inject.Inject

class CertificateInteractor @Inject constructor(
    private val networkDataSource: NetworkDataSource
) {

    suspend fun getCertificate(certificateID: String): DomainCertificate {
        return networkDataSource.getCertificate(certificateID)
    }

    suspend fun getCertificates(): MutableList<UiCertificateList> {
        return networkDataSource.getCertificates()
    }

    suspend fun getCnAndEmailFromCert(certificateString: String): MutableList<String> {
        val certificate = getCertFromString(certificateString) as X509Certificate

        val x500name: X500Name = JcaX509CertificateHolder(certificate).subject
        val stringList = mutableListOf<String>()
        val cn: RDN = x500name.getRDNs(BCStyle.CN)[0]
        stringList.add(cn.first.value.toString())
        val email: RDN = x500name.getRDNs(BCStyle.E)[0]
        stringList.add(email.first.value.toString())

        val publicKey = networkDataSource.getServerPublicKey()
        certificate.verify(publicKey)
        certificate.checkValidity()

        return stringList
    }

    fun getCertFromString(certificateString: String): Certificate {
        return BenoCrypto().stringToCert(certificateString)
    }

    suspend fun encryptMessage(certificateString: String, plainText: String): String { val certificate = getCertFromString(certificateString)
        val partnerPublicKey = certificate.publicKey
        val pemPrivateKey = networkDataSource.getUserPrivateKey()
        val ownPrivateKey = BenoCrypto().pemStringToPrivateKey(pemPrivateKey)
        val signatureBytes = BenoCrypto().signMessage(plainText.toByteArray(), ownPrivateKey)
        val encryptedBytes = BenoCrypto().encryptTextRSA(partnerPublicKey, plainText.toByteArray())
        val outputStream = ByteArrayOutputStream()
        outputStream.write(signatureBytes)
        outputStream.write(encryptedBytes)
        val finalByteArray = outputStream.toByteArray()
        return encodeToString(finalByteArray, Base64.DEFAULT)
    }

    suspend fun decryptMessage(certificateString: String, cipherText: String): String {
        val certificate = getCertFromString(certificateString)
        val partnerPublicKey = certificate.publicKey

        val pemPrivateKey = networkDataSource.getUserPrivateKey()
        val ownPrivateKey = BenoCrypto().pemStringToPrivateKey(pemPrivateKey)

        val wholeByteArray = Base64.decode(cipherText, Base64.DEFAULT)
        val bais = ByteArrayInputStream(wholeByteArray)
        val signatureBytes = ByteArray(256)
        bais.read(signatureBytes)
        val encryptedBytes = bais.readBytes()

        val decryptedBytes = BenoCrypto().decryptTextRSA(ownPrivateKey, encryptedBytes)
        if(!BenoCrypto().verifyMessage(decryptedBytes, partnerPublicKey, signatureBytes)) {
            throw Exception("This message was not sent by the owner of the selected certificate!")
        }
        return String(decryptedBytes)
    }

}