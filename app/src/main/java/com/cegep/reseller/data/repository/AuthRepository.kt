package com.cegep.reseller.data.repository

import android.database.sqlite.SQLiteConstraintException
import com.cegep.reseller.data.PasswordHasher
import com.cegep.reseller.data.SessionManager
import com.cegep.reseller.data.dao.UserDao
import com.cegep.reseller.data.entity.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalCoroutinesApi::class)
class AuthRepository(
    private val userDao: UserDao,
    private val session: SessionManager
) {

    sealed interface AuthResult {
        data class Success(val userId: Long) : AuthResult
        data object InvalidCredentials : AuthResult
        data object EmailTaken : AuthResult
    }

    val currentUser: Flow<User?> = session.currentUserId.flatMapLatest { id ->
        if (id == null) flowOf(null) else userDao.observeById(id)
    }

    suspend fun register(username: String, email: String, password: String): AuthResult = try {
        val id = userDao.insert(
            User(
                username = username.trim(),
                email = email.trim().lowercase(),
                passwordHash = PasswordHasher.hash(password)
            )
        )
        session.setCurrentUser(id)
        AuthResult.Success(id)
    } catch (_: SQLiteConstraintException) {
        AuthResult.EmailTaken
    }

    suspend fun login(email: String, password: String): AuthResult {
        val user = userDao.findByEmail(email.trim().lowercase())
            ?: return AuthResult.InvalidCredentials
        if (!PasswordHasher.matches(password, user.passwordHash)) return AuthResult.InvalidCredentials
        session.setCurrentUser(user.id)
        return AuthResult.Success(user.id)
    }

    suspend fun logout() = session.clear()
}
