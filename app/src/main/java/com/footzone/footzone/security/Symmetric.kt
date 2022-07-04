package com.footzone.footzone.security

import android.os.Build
import androidx.annotation.RequiresApi
import com.footzone.footzone.utils.KeyValues.SECURITY_KEY
import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object Symmetric {

    private var secretKeySpec: SecretKeySpec? = null
    private lateinit var key: ByteArray

    // set Key
    private fun setKey(myKey: String) {
        var sha: MessageDigest? = null
        try {
            key = myKey.toByteArray(charset("UTF-8"))
            sha = MessageDigest.getInstance("SHA-1")
            key = sha.digest(key)
            key = Arrays.copyOf(key, 16)
            secretKeySpec = SecretKeySpec(key, "AES")
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
    }

    // method to encrypt the secret text using key
    @RequiresApi(Build.VERSION_CODES.O)
    fun encrypt(strToEncrypt: String): String? {
        try {
            setKey(SECURITY_KEY)
            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec)
            return Base64.getEncoder().encodeToString(
                cipher.doFinal
                    (strToEncrypt.toByteArray(charset("UTF-8")))
            )
        } catch (e: Exception) {

            println("Error while encrypting: $e")
        }
        return null
    }

    // method to encrypt the secret text using key
    @RequiresApi(Build.VERSION_CODES.O)
    fun decrypt(strToDecrypt: String?): String? {
        try {
            setKey(SECURITY_KEY)
            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec)
            return String(
                cipher.doFinal(
                    Base64.getDecoder().decode(strToDecrypt)
                )
            )
        } catch (e: Exception) {
            println("Error while decrypting: $e")
        }
        return null
    }
}