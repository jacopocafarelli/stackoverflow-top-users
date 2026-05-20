package com.jc.topstackoverflowusers.data.repository

import com.google.common.truth.Truth.assertThat
import com.jc.topstackoverflowusers.data.remote.UsersRemoteDataSource
import com.jc.topstackoverflowusers.data.remote.model.UserResponse
import com.jc.topstackoverflowusers.data.remote.model.UsersPageResponse
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class UsersRepositoryImplTest {

    private val remoteDataSource = mock<UsersRemoteDataSource>()
    private val repository = UsersRepositoryImpl(remoteDataSource)

    @Test
    fun `when remote data source returns users they are correctly mapped to domain models`() = runTest {
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
        whenever(remoteDataSource.getUsers(page = page, pageSize = pageSize))
            .thenReturn(networkResponse)

        val stackOverflowUsers = repository.getTopUsers(page = page, pageSize = pageSize)

        assertThat(stackOverflowUsers.size).isEqualTo(2)
        assertThat(stackOverflowUsers[0].id).isEqualTo(1)
        assertThat(stackOverflowUsers[0].name).isEqualTo("User 1")
        assertThat(stackOverflowUsers[0].profileImageUrl).isEqualTo("url1")
        assertThat(stackOverflowUsers[0].reputation).isEqualTo(100)
        assertThat(stackOverflowUsers[1].id).isEqualTo(2)
        assertThat(stackOverflowUsers[1].name).isEqualTo("User 2")
    }

    @Test
    fun `when remote data source throws an exception it is propagated to the caller`() = runTest {
        val page = 1
        val pageSize = 20
        val expectedException = RuntimeException("Simulated exception")
        whenever(remoteDataSource.getUsers(page = page, pageSize = pageSize))
            .thenThrow(expectedException)

        var thrownException: Exception? = null
        try {
            repository.getTopUsers(page = page, pageSize = pageSize)
        } catch (e: Exception) {
            thrownException = e
        }

        assertThat(thrownException).isNotNull()
        assertThat(thrownException?.message).isEqualTo("Simulated exception")
    }
}