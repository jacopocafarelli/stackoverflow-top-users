package com.jc.topstackoverflowusers.presentation

import com.google.common.truth.Truth.assertThat
import com.jc.topstackoverflowusers.domain.model.StackOverflowUser
import com.jc.topstackoverflowusers.domain.usecase.GetTopUsersUseCase
import com.jc.topstackoverflowusers.presentation.model.TopUsersUiState
import com.jc.topstackoverflowusers.test.rules.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class TopStackoverflowUsersViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val useCase = mock<GetTopUsersUseCase>()

    @Test
    fun `when init block runs and usecase succeeds, state is updated to Success and contains users`() = runTest {
        val expectedUsers = listOf(
            StackOverflowUser(
                id = 1,
                name = "User 1",
                profileImageUrl = "url1",
                reputation = 1000
            )
        )
        whenever(useCase()).thenReturn(Result.success(expectedUsers))

        val viewModel = TopStackoverflowUsersViewModel(useCase)

        val currentState = viewModel.uiState.value
        assertThat(currentState).isInstanceOf(TopUsersUiState.Success::class.java)
        val successState = currentState as TopUsersUiState.Success
        assertThat(successState.users).isEqualTo(expectedUsers)
    }

    @Test
    fun `when init block runs and usecase fails with a message, state is updated to Error`() =
        runTest {
            val exceptionMessage = "Emulated exception"
            val expectedException = RuntimeException(exceptionMessage)
            whenever(useCase()).thenReturn(Result.failure(expectedException))

            val viewModel = TopStackoverflowUsersViewModel(useCase)

            val currentState = viewModel.uiState.value
            assertThat(currentState).isInstanceOf(TopUsersUiState.Error::class.java)
            val errorState = currentState as TopUsersUiState.Error
            assertThat(errorState.message).isEqualTo(exceptionMessage)
        }

    @Test
    fun `when init block runs and usecase fails without a message, state maps to default Error`() =
        runTest {
            val expectedException = RuntimeException()
            whenever(useCase()).thenReturn(Result.failure(expectedException))

            val viewModel = TopStackoverflowUsersViewModel(useCase)

            val currentState = viewModel.uiState.value
            assertThat(currentState).isInstanceOf(TopUsersUiState.Error::class.java)
            val errorState = currentState as TopUsersUiState.Error
            assertThat(errorState.message).isEqualTo("An unexpected error occurred. Please try again.")
        }

    @Test
    fun `when onRetryClicked is called, it loads users`() = runTest {
        val expectedException = RuntimeException("Initial Failure")
        val expectedUsers = listOf(
            StackOverflowUser(
                id = 1,
                name = "Test User",
                profileImageUrl = "url",
                reputation = 100
            )
        )
        whenever(useCase()).thenReturn(
            Result.failure(expectedException),
            Result.success(expectedUsers)
        )

        val viewModel = TopStackoverflowUsersViewModel(useCase)

        assertThat(viewModel.uiState.value).isInstanceOf(TopUsersUiState.Error::class.java)

        viewModel.onRetryClicked()

        val currentState = viewModel.uiState.value
        assertThat(currentState).isInstanceOf(TopUsersUiState.Success::class.java)
        val successState = currentState as TopUsersUiState.Success
        assertThat(successState.users).isEqualTo(expectedUsers)
        // Use case is called in init and on retry
        verify(useCase, times(2)).invoke()
    }
}