package com.cegep.reseller.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cegep.reseller.data.AppDatabase
import com.cegep.reseller.data.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthRepositoryTest {

    private lateinit var db: AppDatabase
    private lateinit var session: SessionManager
    private lateinit var repo: AuthRepository

    @Before
    fun setup() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        session = SessionManager(context)
        session.clear()
        repo = AuthRepository(db.userDao(), session)
    }

    @After
    fun teardown() = runBlocking {
        session.clear()
        db.close()
    }

    @Test
    fun registerThenLoginRoundTrips() = runTest {
        val registered = repo.register("Luca", "luca@test.com", "password123")
        assertTrue(registered is AuthRepository.AuthResult.Success)

        repo.logout()
        assertNull(repo.currentUser.first())

        val loggedIn = repo.login("luca@test.com", "password123")
        assertTrue(loggedIn is AuthRepository.AuthResult.Success)

        val user = repo.currentUser.first { it != null }
        assertEquals("luca@test.com", user!!.email)
        assertEquals("Luca", user.username)
    }

    @Test
    fun emailIsNormalisedToLowercase() = runTest {
        repo.register("Mixed", "Mixed@Test.com", "password")
        val result = repo.login("MIXED@test.com", "password")
        assertTrue(result is AuthRepository.AuthResult.Success)
    }

    @Test
    fun duplicateEmailIsRejected() = runTest {
        repo.register("First", "dup@test.com", "password")
        val second = repo.register("Second", "dup@test.com", "password")
        assertTrue(second is AuthRepository.AuthResult.EmailTaken)
    }

    @Test
    fun loginWithWrongPasswordFails() = runTest {
        repo.register("X", "x@test.com", "rightpass")
        val result = repo.login("x@test.com", "wrongpass")
        assertTrue(result is AuthRepository.AuthResult.InvalidCredentials)
    }

    @Test
    fun loginUnknownEmailFails() = runTest {
        val result = repo.login("ghost@test.com", "anything")
        assertTrue(result is AuthRepository.AuthResult.InvalidCredentials)
    }
}
