package com.jc.topstackoverflowusers.domain.repository

import com.jc.topstackoverflowusers.domain.model.StackOverflowUser

interface UsersRepository {
    suspend fun getTopUsers(page: Int, pageSize: Int): List<StackOverflowUser>
}