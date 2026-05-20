package com.jc.topstackoverflowusers.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val FOLLOWED_USER_IDS = stringSetPreferencesKey("followed_user_ids")

class UsersFollowLocalDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    fun observeFollowedUsers(): Flow<Set<Int>> {
        return dataStore.data.map { prefs ->
            prefs[FOLLOWED_USER_IDS]
                .orEmpty()
                .mapNotNullTo(mutableSetOf()) { it.toIntOrNull() }
        }
    }

    suspend fun followUser(userId: Int) {
        dataStore.edit { prefs ->
            val current = prefs[FOLLOWED_USER_IDS].orEmpty().toMutableSet()
            current.add(userId.toString())
            prefs[FOLLOWED_USER_IDS] = current
        }
    }

    suspend fun unfollowUser(userId: Int) {
        dataStore.edit { prefs ->
            val current = prefs[FOLLOWED_USER_IDS].orEmpty().toMutableSet()
            current.remove(userId.toString())
            prefs[FOLLOWED_USER_IDS] = current
        }
    }
}