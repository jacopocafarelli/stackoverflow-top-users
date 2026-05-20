package com.jc.topstackoverflowusers.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.jc.topstackoverflowusers.domain.model.StackOverflowUser
import com.jc.topstackoverflowusers.domain.repository.UsersRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class GetTopUsersUseCaseTest {

    private val repository = mock<UsersRepository>()
    private val useCase = GetTopUsersUseCase(repository)

    @Test
    fun `when repository call succeeds invoke returns success result with users`() = runTest {
        val expectedUsers = listOf(
            StackOverflowUser(
                id = 1,
                name = "Test User",
                profileImageUrl = "url",
                reputation = 100,
                isFollowed = true
            )
        )
        whenever(repository.getTopUsers(page = 1, pageSize = 20))
            .thenReturn(flowOf(expectedUsers))

        val result = useCase().first()

        assertThat(result).isEqualTo(expectedUsers)
    }

    @Test(expected = RuntimeException::class)
    fun `when repository throws exception invoke propagates it`() = runTest {
        val expectedException = RuntimeException("Simulated exception")
        whenever(repository.getTopUsers(page = 1, pageSize = 20))
            .thenReturn(flow { throw expectedException })

        useCase().first()
    }
}