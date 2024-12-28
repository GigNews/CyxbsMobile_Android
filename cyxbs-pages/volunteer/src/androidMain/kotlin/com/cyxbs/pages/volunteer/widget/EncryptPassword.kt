package com.cyxbs.pages.volunteer.widget

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


class EncryptPassword {
    companion object {
        private val IV_STRING = "zhangshangcquptv"
        private val charset = "UTF-8"
        val KEY = "redrockvolunteer"

        fun encrypt(content: String): String {
            val contentBytes = content.toByteArray(charset(charset))
            val keyBytes = KEY.toByteArray(charset(charset))
            val encryptedBytes = aesEncryptBytes(contentBytes, keyBytes)
            return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
        }

        private fun aesEncryptBytes(contentBytes: ByteArray, keyBytes: ByteArray): ByteArray {
            return cipherOperation(contentBytes, keyBytes, Cipher.ENCRYPT_MODE)
        }

        private fun cipherOperation(contentBytes: ByteArray, keyBytes: ByteArray, mode: Int): ByteArray {
            val secretKey = SecretKeySpec(keyBytes, "AES")

            val initParam = IV_STRING.toByteArray(charset(charset))
            val ivParameterSpec = IvParameterSpec(initParam)

            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(mode, secretKey, ivParameterSpec)

            return cipher.doFinal(contentBytes)
        }
    }
}