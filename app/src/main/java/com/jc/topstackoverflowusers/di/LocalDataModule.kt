package com.jc.topstackoverflowusers.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val NAME = "followed_users"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(NAME)

@Module
@InstallIn(SingletonComponent::class)
object LocalDataModule {

    @Provides
    @Singleton
    fun provideFollowedUsersDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> = context.dataStore
}