package com.example.cryptoszd.ui.messageencryption.certificate

import android.util.Base64
import android.util.Base64.encodeToString
import co.zsmb.rainbowcake.withIOContext
import com.example.cryptoszd.domain.interactor.CertificateInteractor
import com.example.cryptoszd.domain.interactor.UserInteractor
import com.example.cryptoszd.domain.model.DomainCertificate
import com.example.cryptoszd.ui.model.UiCertificate
import org.spongycastle.asn1.x500.RDN
import org.spongycastle.asn1.x500.X500Name
import java.security.cert.X509Certificate
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import org.spongycastle.asn1.x500.style.BCStyle
import org.spongycastle.cert.jcajce.JcaX509CertificateHolder

class CertificatePresenter @Inject constructor(
    private val certificateInteractor: CertificateInteractor
) {

    suspend fun getCertificate(certificateID: String): UiCertificate = withIOContext {
        certificateInteractor.getCertificate(certificateID).toUiCertificate()
    }

    private fun DomainCertificate.toUiCertificate(): UiCertificate {
        val cert = certificate as X509Certificate
        val sx500name: X500Name = JcaX509CertificateHolder(cert).subject
        val scn: RDN = sx500name.getRDNs(BCStyle.CN)[0]
        val sc: RDN = sx500name.getRDNs(BCStyle.C)[0]
        val sl: RDN = sx500name.getRDNs(BCStyle.L)[0]
        val semail: RDN = sx500name.getRDNs(BCStyle.E)[0]

        val ix500name: X500Name = JcaX509CertificateHolder(cert).issuer
        val icn: RDN = ix500name.getRDNs(BCStyle.CN)[0]
        val ic: RDN = ix500name.getRDNs(BCStyle.C)[0]
        val il: RDN = ix500name.getRDNs(BCStyle.L)[0]
        val io: RDN = ix500name.getRDNs(BCStyle.O)[0]

        val sdf = SimpleDateFormat("yyyy. MM. dd. HH:mm z", Locale.getDefault())
        val notBefore = cert.notBefore
        val notAfter = cert.notAfter
        return UiCertificate(
            certificate = encodeToString(cert.encoded, Base64.DEFAULT),
            valid = valid,
            expired = expired,
            subjectCommonName = scn.first.value.toString(),
            subjectCountry = sc.first.value.toString(),
            subjectEmail = semail.first.value.toString(),
            subjectLocality = sl.first.value.toString(),
            issuerCommonName = icn.first.value.toString(),
            issuerCountry = ic.first.value.toString(),
            issuerLocality = il.first.value.toString(),
            issuerOrganization = io.first.value.toString(),
            publicKey = encodeToString(cert.publicKey.encoded, Base64.DEFAULT),
            signatureAlgorithm = cert.sigAlgName,
            signature = encodeToString(cert.signature, Base64.DEFAULT),
            validNotBefore = notBefore.toDateString(sdf),
            validNotAfter = notAfter.toDateString(sdf),
            serialNumber = cert.serialNumber.toString(),
            version = cert.version.toString(),
            type = cert.type
        )
    }

    private fun Date?.toDateString(simpleDateFormat: SimpleDateFormat): String {
        return if (this != null) {
            simpleDateFormat.format(this)
        } else {
            "N/A"
        }
    }
}