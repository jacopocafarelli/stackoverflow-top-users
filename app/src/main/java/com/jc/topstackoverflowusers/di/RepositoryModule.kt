package com.jc.topstackoverflowusers.di

import com.jc.topstackoverflowusers.data.repository.UsersRepositoryImpl
import com.jc.topstackoverflowusers.domain.repository.UsersRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(userRepositoryImpl: UsersRepositoryImpl): UsersRepository
}