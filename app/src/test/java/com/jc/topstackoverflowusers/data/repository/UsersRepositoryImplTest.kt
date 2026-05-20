package com.jc.topstackoverflowusers.data.repository

import com.google.common.truth.Truth.assertThat
import com.jc.topstackoverflowusers.data.local.UsersFollowLocalDataSource
import com.jc.topstackoverflowusers.data.remote.UsersRemoteDataSource
import com.jc.topstackoverflowusers.data.remote.model.UserResponse
import com.jc.topstackoverflowusers.data.remote.model.UsersPageResponse
import com.jc.topstackoverflowusers.domain.model.StackOverflowUser
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class UsersRepositoryImplTest {

    private val remoteDataSource = mock<UsersRemoteDataSource>()
    private val localDataSource = mock<UsersFollowLocalDataSource>()
    private val repository = UsersRepositoryImpl(remoteDataSource, localDataSource)

    @Test
    fun `when remote data source returns users, they are correctly mapped with follow status`() =
        runTest {
            val page = 1
            val pageSize = 20
            val usersResponse = listOf(
                UserResponse(
                    accountId = 1,
                    displayName = "User 1",
                    profileImage = "url1",
                    reputation = 100
                ),
                UserResponse(
                    accountId = 2,
                    displayName = "User 2",
                    profileImage = "url2",
                    reputation = 200
                )
            )
            val networkResponse = UsersPageResponse(users = usersResponse, hasMore = true)
            whenever(remoteDataSource.getUsers(page, pageSize)).thenReturn(networkResponse)
            whenever(localDataSource.observeFollowedUsers()).thenReturn(flowOf(setOf(1)))

            val topUsersFlow = repository.getTopUsers(page, pageSize)
            val result = topUsersFlow.first()

            assertThat(result).hasSize(2)
            assertThat(result[0].id).isEqualTo(1)
            assertThat(result[0].isFollowed).isTrue()
            assertThat(result[1].id).isEqualTo(2)
            assertThat(result[1].isFollowed).isFalse()
        }

    @Test
    fun `when local followed list changes, repository emits updated list`() = runTest {
        val page = 1
        val pageSize = 20
        val usersResponse = listOf(
            UserResponse(
                accountId = 1,
                displayName = "User 1",
                profileImage = "url1",
                reputation = 100
            )
        )
        whenever(remoteDataSource.getUsers(page, pageSize)).thenReturn(
            UsersPageResponse(
                usersResponse,
                true
            )
        )
        whenever(localDataSource.observeFollowedUsers()).thenReturn(flowOf(setOf(1), emptySet()))

        val topUsersFlow = repository.getTopUsers(page, pageSize)
        val results = mutableListOf<List<StackOverflowUser>>()
        topUsersFlow.take(2).collect { results.add(it) }

        assertThat(results[0][0].isFollowed).isTrue()
        assertThat(results[1][0].isFollowed).isFalse()
    }

    @Test
    fun `when remote data source throws an exception it is propagated to the caller`() = runTest {
        val page = 1
        val pageSize = 20
        val expectedException = RuntimeException("Simulated exception")
        whenever(remoteDataSource.getUsers(page = page, pageSize = pageSize))
            .thenThrow(expectedException)
        whenever(localDataSource.observeFollowedUsers())
            .thenReturn(flowOf(emptySet()))

        var thrownException: Exception? = null
        try {
            repository.getTopUsers(page, pageSize).collect { }
        } catch (e: Exception) {
            thrownException = e
        }

        assertThat(thrownException).isNotNull()
        assertThat(thrownException?.message).isEqualTo("Simulated exception")
    }

    @Test
    fun `when following operation is performed by local data source`() = runTest {
        val userId = 1

        repository.followUser(userId)

        verify(localDataSource).followUser(userId)
    }

    @Test
    fun `when unfollowing operation is performed by local data source`() = runTest {
        val userId = 1

        repository.unfollowUser(userId)

        verify(localDataSource).unfollowUser(userId)
    }
}