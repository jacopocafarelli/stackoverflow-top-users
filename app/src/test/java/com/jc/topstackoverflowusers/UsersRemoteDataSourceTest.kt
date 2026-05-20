package com.jc.topstackoverflowusers

import com.google.common.truth.Truth.assertThat
import com.jc.topstackoverflowusers.data.remote.UsersRemoteDataSource
import com.jc.topstackoverflowusers.data.remote.UsersService
import com.jc.topstackoverflowusers.data.remote.model.UserResponse
import com.jc.topstackoverflowusers.data.remote.model.UsersResponse
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class UsersRemoteDataSourceTest {

    private val service = mock<UsersService>()
    private val dataSource = UsersRemoteDataSource(service)

    @Test
    fun `when response contains items and hasMore they are returned correctly in the result`() = runTest {
        val page = 1
        val pageSize = 20
        val items = listOf(
            UserResponse(accountId = 1, displayName = "User 1", profileImage = "url1", reputation = 100),
            UserResponse(accountId = 2, displayName = "User 2", profileImage = "url2", reputation = 200)
        )
        val response = UsersResponse(items = items, hasMore = true)
        whenever(service.getUsers(page = page, pageSize = pageSize)).thenReturn(response)

        val result = dataSource.getUsers(page = page, pageSize = pageSize)

        assertThat(result.users).isEqualTo(items)
        assertThat(result.hasMore).isEqualTo(true)
    }

    @Test
    fun `when response contains a null hasMore it is returned as false in the result`() = runTest {
        val page = 1
        val pageSize = 20
        val response = UsersResponse(items = emptyList(), hasMore = null)
        whenever(service.getUsers(page = page, pageSize = pageSize)).thenReturn(response)

        val result = dataSource.getUsers(page = page, pageSize = pageSize)

        assertThat(result.hasMore).isEqualTo(false)
    }
}