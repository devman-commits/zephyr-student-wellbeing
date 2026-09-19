package com.zephyr.wellbeing.data

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object CryptoUtils {

    private fun deriveKey(passkey: String): SecretKeySpec {
        val digest = MessageDigest.getInstance("SHA-256")
        val keyBytes = digest.digest(passkey.trim().uppercase().toByteArray(Charsets.UTF_8))
        return SecretKeySpec(keyBytes, "AES")
    }

    fun encrypt(plainText: String, passkey: String): String {
        return try {
            val keySpec = deriveKey(passkey)
            val iv = ByteArray(16)
            SecureRandom().nextBytes(iv)
            val ivSpec = IvParameterSpec(iv)

            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
            val cipherBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))

            val ivBase64 = Base64.encodeToString(iv, Base64.NO_WRAP)
            val cipherBase64 = Base64.encodeToString(cipherBytes, Base64.NO_WRAP)
            "$ivBase64:$cipherBase64"
        } catch (e: Exception) {
            plainText
        }
    }

    fun decrypt(payload: String, passkey: String): String {
        return try {
            val parts = payload.split(":")
            if (parts.size != 2) return payload

            val iv = Base64.decode(parts[0], Base64.NO_WRAP)
            val cipherBytes = Base64.decode(parts[1], Base64.NO_WRAP)

            val keySpec = deriveKey(passkey)
            val ivSpec = IvParameterSpec(iv)

            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
            val decryptedBytes = cipher.doFinal(cipherBytes)
            String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            "🔒 [Decryption Failed: Invalid Passkey]"
        }
    }

    fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(password.toByteArray(Charsets.UTF_8))
        return hash.joinToString("") { "%02x".format(it) }
    }
}
