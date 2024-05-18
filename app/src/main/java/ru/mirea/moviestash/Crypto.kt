package ru.mirea.moviestash

import java.security.MessageDigest
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object Crypto {

    fun encrypt(input: String, password: String): String {

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val keySpec = SecretKeySpec(password.toByteArray(), "AES")
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, IvParameterSpec(ByteArray(16)))
        val encrypt = cipher.doFinal(input.toByteArray())
        val result = Base64.getEncoder().encode(encrypt)

        return String(result)
    }

    fun decrypt(input: String, password: ByteArray): String {

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val keySpec = SecretKeySpec(password, "AES")
        cipher.init(Cipher.DECRYPT_MODE, keySpec, IvParameterSpec(ByteArray(16)))
        val encrypt = cipher.doFinal(Base64.getDecoder().decode(input.toByteArray()))

        return String(encrypt)
    }

    fun getShaHash(password: String, login: String): String {
        return MessageDigest.getInstance("SHA-256").digest("$password$login".toByteArray()).toHex()
    }

    fun ByteArray.toHex(): String {
        return joinToString("") { "%02x".format(it) }
    }

}