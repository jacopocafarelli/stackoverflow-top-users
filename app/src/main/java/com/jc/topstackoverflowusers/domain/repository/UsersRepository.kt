package com.jc.topstackoverflowusers.domain.repository

import com.jc.topstackoverflowusers.domain.model.StackOverflowUser
import kotlinx.coroutines.flow.Flow

interface UsersRepository {
    suspend fun getTopUsers(page: Int, pageSize: Int): Flow<List<StackOverflowUser>>
    suspend fun followUser(accountId: Int)
    suspend fun unfollowUser(accountId: Int)
}