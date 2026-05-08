package com.cegep.reseller.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PasswordHasherTest {

    @Test
    fun sameInputProducesSameHash() {
        assertEquals(PasswordHasher.hash("hunter2"), PasswordHasher.hash("hunter2"))
    }

    @Test
    fun differentInputsProduceDifferentHashes() {
        assertNotEquals(PasswordHasher.hash("alpha"), PasswordHasher.hash("beta"))
    }

    @Test
    fun matchesReturnsTrueForCorrectPassword() {
        val hash = PasswordHasher.hash("correct horse battery staple")
        assertTrue(PasswordHasher.matches("correct horse battery staple", hash))
    }

    @Test
    fun matchesReturnsFalseForIncorrectPassword() {
        val hash = PasswordHasher.hash("correct")
        assertFalse(PasswordHasher.matches("incorrect", hash))
    }

    @Test
    fun hashIsHexEncoded() {
        val hash = PasswordHasher.hash("anything")
        assertTrue(hash.matches(Regex("[0-9a-f]+")))
        assertEquals(64, hash.length)
    }
}
