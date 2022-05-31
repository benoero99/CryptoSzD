package com.example.cryptomod

import android.text.TextUtils
import android.util.Base64
import android.util.Log
import java.io.*
import java.lang.Exception
import java.security.*
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import java.security.spec.InvalidKeySpecException
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.*
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec

class BenoCrypto {

    fun createRsaKeyPair(): KeyPair {
        val random = SecureRandom()
        val keyPairGenerator: KeyPairGenerator = KeyPairGenerator.getInstance("RSA")
        keyPairGenerator.initialize(1024, random)
        return keyPairGenerator.generateKeyPair()
    }

//    fun createX509Certificate(dn: DistinguishedName, validityTime: Long, keyPair: KeyPair): X509Certificate {
//        var x500NameString = "CN=${dn.commonName}"
//        if(dn.location.isNotEmpty()) {
//            x500NameString += ",L=${dn.location}"
//        }
//        if(dn.countryCode.isNotEmpty()) {
//            x500NameString += ",C=${dn.countryCode}"
//        }
//        if(dn.email.isNotEmpty()) {
//            x500NameString += ",E=${dn.email}"
//        }
//        val x500Name = X500Name(x500NameString)
//        val provider = BouncyCastleProvider()
//        val validityBeginDate = Date(System.currentTimeMillis())
//        val validityEndDate = Date(System.currentTimeMillis() + validityTime)
//
//    }

//    fun pemToCer(pem: String): X509Certificate {
//        try {
//            val provider = BouncyCastleProvider()
//            val reader = StringReader(pem)
//            val pemParser = PEMParser(reader)
//            val certHolder = pemParser.readObject() as X509CertificateHolder
//            return JcaX509CertificateConverter().setProvider(provider).getCertificate(certHolder)
//        } catch (e: Exception) {
//            throw e
//        }
//    }
//
//    fun pemToPublicKey(pem: String): PublicKey {
//        val reader = StringReader(pem)
//        val pemParser = PEMParser(reader)
//        val converter = JcaPEMKeyConverter()
//        val publicKeyInfo = pemParser.readObject() as SubjectPublicKeyInfo
//        return  converter.getPublicKey(publicKeyInfo) as PublicKey
//    }
//
//    fun pemToPrivateKey(pem: String): PrivateKey {
//        val reader = StringReader(pem)
//        val pemParser = PEMParser(reader)
//        val converter = JcaPEMKeyConverter()
//        val pemKeyPair = pemParser.readObject() as PEMKeyPair
//        val privateKeyInfo = pemKeyPair.privateKeyInfo
//        return converter.getPrivateKey(privateKeyInfo) as PrivateKey
//    }

    fun pemStringToString(pem: String): String {
        val pem2 = pem.replace("\r", "")
        val list = pem2.split("\n") as MutableList
        list.removeFirst()
        list.removeLast()
        list.removeLast()
        return TextUtils.join("", list)
    }

    fun pemStringToPublicKey(pem: String): PublicKey {
        val publicKeyString = pemStringToString(pem)
        val serverPublicKeyBytes = Base64.decode(publicKeyString, Base64.DEFAULT)
        val publicKeySpec = X509EncodedKeySpec(serverPublicKeyBytes)
        val kf: KeyFactory = KeyFactory.getInstance("RSA")
        return kf.generatePublic(publicKeySpec)
    }

    fun pemStringToPrivateKey(pem: String): PrivateKey {
        val privateKeyString = BenoCrypto().pemStringToString(pem)
        val privateKeyBytes = Base64.decode(privateKeyString, Base64.DEFAULT)
        val keySpec = PKCS8EncodedKeySpec(privateKeyBytes)
        val kf = KeyFactory.getInstance("RSA")
        return kf.generatePrivate(keySpec)
    }

    fun pemStringToCert(pemCertificateString: String): Certificate {
        val cf: CertificateFactory = CertificateFactory.getInstance("X.509")
        val bais = ByteArrayInputStream(pemCertificateString.toByteArray())
        return cf.generateCertificate(bais)
    }

    fun stringToCert(certificateString: String): Certificate {
        val pemString = "-----BEGIN CERTIFICATE-----\n$certificateString\n-----END CERTIFICATE-----\n"
        return pemStringToCert(pemString)
    }

    fun verifyCertificate(certificate: Certificate, publicKey: PublicKey, signatureBytes: ByteArray): Boolean {
        Log.d("called",signatureBytes.size.toString() )
        val signature = Signature.getInstance("SHA256withRSA")
        signature.initVerify(publicKey)
        signature.update(certificate.encoded)
        return signature.verify(signatureBytes)
    }

    fun encryptTextByPassword(plainText: String, password: CharArray) : String {
        val salt = randomBytes()
        val iv = randomBytes()

        val secretKey = generatePBESecretKey(password, salt)
        val cipher: Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, IvParameterSpec(iv))

        val baos = ByteArrayOutputStream()
        baos.write(salt)
        baos.write(iv)
        baos.write(cipher.doFinal(plainText.toByteArray()))
        baos.flush()
        val cipherText = baos.toByteArray()
        baos.close()
        return Base64.encodeToString(cipherText, Base64.DEFAULT)
    }

    fun decryptTextByPassword(cipherText: String, password: CharArray) : String {
        val bytes = Base64.decode(cipherText, Base64.DEFAULT)
        val bais = ByteArrayInputStream(bytes)
        val salt = ByteArray(16)
        val iv = ByteArray(16)
        bais.read(salt)
        bais.read(iv)
        val encryptedBytes = bais.readBytes()

        val secretKey = generatePBESecretKey(password, salt)
        val cipher: Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(iv))
        val decryptedBytes: ByteArray
        try {
            decryptedBytes = cipher.doFinal(encryptedBytes)
        } catch (e: BadPaddingException) {
            throw BadPaddingException("Wrong password!")
        }

        return String(decryptedBytes)
    }

    private fun randomBytes(): ByteArray {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return bytes
    }

    private fun generatePBESecretKey(password: CharArray, salt: ByteArray) : SecretKey {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val pbeKeySpec = PBEKeySpec(password, salt, 5000, 128)
        val secretKey: SecretKey
        try {
            secretKey = factory.generateSecret(pbeKeySpec)
        } catch (e: InvalidKeySpecException) {
            throw e
        }
        pbeKeySpec.clearPassword()
        return secretKey
    }

    fun encryptFileByPassword(inputFile: File, password: CharArray, outputFile: File) {
        val iv = randomBytes()
        val salt = randomBytes()

        val secretKey = generatePBESecretKey(password, salt)

        val fis = FileInputStream(inputFile)
        val fos = FileOutputStream(outputFile)

        val cipher: Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, IvParameterSpec(iv))
        val cos = CipherOutputStream(fos, cipher)
        fos.write(salt)
        fos.write(iv)
        var b: Int
        val d = ByteArray(1024*8)

        while (fis.read(d).also { b = it } != -1) {
            cos.write(d, 0, b)
        }
        cos.flush()
        cos.close()
        fis.close()
    }

    fun decryptFileByPassword(inputFile: File, password: CharArray, outputFile: File) {
        val fis = FileInputStream(inputFile)
        val fos = FileOutputStream(outputFile)

        // Files that encrypted by this module have a salt and an IV at the beginning of the file
        // The first 16 byte is the salt, second 16 byte is the IV
        val salt = ByteArray(16)
        fis.read(salt)
        val iv = ByteArray(16)
        fis.read(iv)

        val secretKey = generatePBESecretKey(password, salt)

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(iv))

        // In javax.crypto.CipherInputStream the internal buffer size is 512 byte so I copied
        // the class and increased it to 8192
        val cis = CipherInputStream(fis, cipher)
        var b: Int
        val d = ByteArray(1024*8)

        try {
            while (cis.read(d).also { b = it } != -1) {
                fos.write(d, 0, b)
            }
        } catch (e: IOException) {
            outputFile.delete()
            throw BadPaddingException("Wrong Password")
        } finally {
            fos.flush()
            fos.close()
            cis.close()
        }
    }

    fun signMessage(plainTextBytes: ByteArray, privateKey: PrivateKey): ByteArray {
        val signature = Signature.getInstance("SHA256withRSA")
        signature.initSign(privateKey)
        signature.update(plainTextBytes)
        return signature.sign()
    }

    fun verifyMessage(plainTextBytes: ByteArray, publicKey: PublicKey, signatureBytes: ByteArray): Boolean {
        val signature = Signature.getInstance("SHA256withRSA")
        signature.initVerify(publicKey)
        signature.update(plainTextBytes)
        return signature.verify(signatureBytes)
    }

    fun encryptTextRSA(publicKey: PublicKey, plainText: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        return cipher.doFinal(plainText)
    }

    fun decryptTextRSA(privateKey: PrivateKey, cipherText: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING")
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        return try {
            cipher.doFinal(cipherText)
        } catch (e: Exception) {
            throw Exception("Message decryption failed", e)
        }
    }




}