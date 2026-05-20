package com.jc.topstackoverflowusers.data.repository

import com.jc.topstackoverflowusers.data.remote.UsersRemoteDataSource
import com.jc.topstackoverflowusers.domain.model.StackOverflowUser
import com.jc.topstackoverflowusers.domain.repository.UsersRepository
import javax.inject.Inject

class UsersRepositoryImpl @Inject constructor(
    private val remoteDataSource: UsersRemoteDataSource
) : UsersRepository {

    override suspend fun getTopUsers(page: Int, pageSize: Int): List<StackOverflowUser> {
        val response = remoteDataSource.getUsers(page, pageSize)

        return response.users.map { user ->
            StackOverflowUser(
                id = user.accountId,
                name = user.displayName,
                profileImageUrl = user.profileImage,
                reputation = user.reputation
            )
        }
    }
}