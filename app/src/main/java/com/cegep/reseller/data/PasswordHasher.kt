package com.cegep.reseller.data

import java.security.MessageDigest

object PasswordHasher {

    private const val SALT = "reseller-cegep-2026"

    fun hash(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest((SALT + password).toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun matches(password: String, hash: String): Boolean = hash(password) == hash
}
