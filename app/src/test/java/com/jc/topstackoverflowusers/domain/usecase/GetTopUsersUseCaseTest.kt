package com.jc.topstackoverflowusers.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.jc.topstackoverflowusers.domain.model.StackOverflowUser
import com.jc.topstackoverflowusers.domain.repository.UsersRepository
import kotlinx.coroutines.CancellationException
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
                reputation = 100
            )
        )
        whenever(repository.getTopUsers(page = 1, pageSize = 20)).thenReturn(expectedUsers)

        val result = useCase()

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(expectedUsers)
    }

    @Test
    fun `when repository throws a generic exception invoke returns failure result`() = runTest {
        val expectedException = RuntimeException("Simulated exception")
        whenever(repository.getTopUsers(page = 1, pageSize = 20)).thenThrow(expectedException)

        val result = useCase()

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Simulated exception")
    }

    @Test(expected = CancellationException::class)
    fun `when CancellationException invoke rethrows it to maintain structured concurrency`() = runTest {
        whenever(repository.getTopUsers(page = 1, pageSize = 20))
            .thenThrow(CancellationException())

        useCase()
    }
}