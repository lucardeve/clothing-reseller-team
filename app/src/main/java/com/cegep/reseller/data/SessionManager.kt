package com.cegep.reseller.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "session")

class SessionManager(private val context: Context) {

    private val keyUserId = longPreferencesKey("current_user_id")

    val currentUserId: Flow<Long?> = context.dataStore.data.map { prefs ->
        val id = prefs[keyUserId] ?: 0L
        if (id == 0L) null else id
    }

    suspend fun setCurrentUser(userId: Long) {
        context.dataStore.edit { it[keyUserId] = userId }
    }

    suspend fun clear() {
        context.dataStore.edit { it.remove(keyUserId) }
    }
}
