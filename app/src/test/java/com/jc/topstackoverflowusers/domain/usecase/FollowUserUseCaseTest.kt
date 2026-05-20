package com.jc.topstackoverflowusers.domain.usecase

import com.jc.topstackoverflowusers.domain.repository.UsersRepository
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class FollowUserUseCaseTest {

    private val repository = mock<UsersRepository>()
    private val useCase = FollowUserUseCase(repository)

    @Test
    fun `when invoke is called, repository followUser is triggered with correct id`() = runTest {
        val testAccountId = 1

        useCase(testAccountId)

        verify(repository).followUser(testAccountId)
    }
}