package com.jc.topstackoverflowusers.data.repository

import com.jc.topstackoverflowusers.data.local.UsersFollowLocalDataSource
import com.jc.topstackoverflowusers.data.remote.UsersRemoteDataSource
import com.jc.topstackoverflowusers.domain.model.StackOverflowUser
import com.jc.topstackoverflowusers.domain.repository.UsersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class UsersRepositoryImpl @Inject constructor(
    private val remoteDataSource: UsersRemoteDataSource,
    private val localDataSource: UsersFollowLocalDataSource
) : UsersRepository {

    private val domainUsersFlow = MutableStateFlow<List<StackOverflowUser>>(emptyList())

    override suspend fun getTopUsers(page: Int, pageSize: Int): Flow<List<StackOverflowUser>> {
        val response = remoteDataSource.getUsers(page, pageSize)

        domainUsersFlow.value = response.users.map { user ->
            StackOverflowUser(
                id = user.accountId,
                name = user.displayName,
                profileImageUrl = user.profileImage,
                reputation = user.reputation,
                isFollowed = false
            )
        }
        return combine(
            domainUsersFlow,
            localDataSource.observeFollowedUsers()
        ) { domainUsers, followedIds ->
            domainUsers.map { user ->
                user.copy(isFollowed = followedIds.contains(user.id))
            }
        }
    }

    override suspend fun followUser(accountId: Int) = localDataSource.followUser(accountId)

    override suspend fun unfollowUser(accountId: Int) = localDataSource.unfollowUser(accountId)
}