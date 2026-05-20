package com.jc.topstackoverflowusers.presentation

import com.google.common.truth.Truth.assertThat
import com.jc.topstackoverflowusers.domain.model.StackOverflowUser
import com.jc.topstackoverflowusers.domain.usecase.FollowUserUseCase
import com.jc.topstackoverflowusers.domain.usecase.GetTopUsersUseCase
import com.jc.topstackoverflowusers.domain.usecase.UnfollowUserUseCase
import com.jc.topstackoverflowusers.presentation.model.ErrorType
import com.jc.topstackoverflowusers.presentation.model.TopUsersUiState
import com.jc.topstackoverflowusers.test.rules.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.IOException
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import retrofit2.HttpException
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class TopStackoverflowUsersViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getUsersUseCase = mock<GetTopUsersUseCase>()
    private val followUserUseCase = mock<FollowUserUseCase>()
    private val unfollowUserUseCase = mock<UnfollowUserUseCase>()

    @Test
    fun `when init block runs and getting user succeeds, state is updated to Success and contains users`() =
        runTest {
            val expectedUsers = listOf(
                StackOverflowUser(
                    id = 1,
                    name = "User 1",
                    profileImageUrl = "url1",
                    reputation = 1000,
                    isFollowed = true
                )
            )
            whenever(getUsersUseCase()).thenReturn(flowOf(expectedUsers))

            val viewModel = createViewModel()

            val currentState = viewModel.uiState.value
            assertThat(currentState).isInstanceOf(TopUsersUiState.Success::class.java)
            val successState = currentState as TopUsersUiState.Success
            assertThat(successState.users).isEqualTo(expectedUsers)
        }

    @Test
    fun `when init block runs and getting user fails with io error, state maps to network Error`() =
        runTest {
            val exceptionMessage = "Emulated exception"
            val expectedException = IOException(exceptionMessage)
            whenever(getUsersUseCase()).thenReturn(flow { throw expectedException })

            val viewModel = createViewModel()

            val currentState = viewModel.uiState.value
            assertThat(currentState).isInstanceOf(TopUsersUiState.Error::class.java)
            val errorState = currentState as TopUsersUiState.Error
            assertThat(errorState.errorType).isEqualTo(ErrorType.NETWORK)
        }

    @Test
    fun `when init block runs and getting user fails with http error, state maps to network Error`() =
        runTest {
            val errorResponse = Response.error<Any>(
                500,
                "{\"error\": \"Generic error\"}".toResponseBody(null)
            )
            val expectedException = HttpException(errorResponse)
            whenever(getUsersUseCase()).thenReturn(flow { throw expectedException })

            val viewModel = createViewModel()

            val currentState = viewModel.uiState.value
            assertThat(currentState).isInstanceOf(TopUsersUiState.Error::class.java)
            val errorState = currentState as TopUsersUiState.Error
            assertThat(errorState.errorType).isEqualTo(ErrorType.SERVER)
        }

    @Test
    fun `when init block runs and getting user fails with generic error, state maps to default Error`() =
        runTest {
            val expectedException = RuntimeException()
            whenever(getUsersUseCase()).thenReturn(flow { throw expectedException })

            val viewModel = createViewModel()

            val currentState = viewModel.uiState.value
            assertThat(currentState).isInstanceOf(TopUsersUiState.Error::class.java)
            val errorState = currentState as TopUsersUiState.Error
            assertThat(errorState.errorType).isEqualTo(ErrorType.GENERIC)
        }

    @Test
    fun `when onRetryClicked is called, it re-collects users`() = runTest {
        val expectedUsers = listOf(
            StackOverflowUser(
                id = 1,
                name = "User 1",
                profileImageUrl = "url1",
                reputation = 1000,
                isFollowed = true
            )
        )
        whenever(getUsersUseCase()).thenReturn(
            flow { throw RuntimeException("Fail") },
            flowOf(expectedUsers)
        )

        val viewModel = createViewModel()
        assertThat(viewModel.uiState.value).isInstanceOf(TopUsersUiState.Error::class.java)

        viewModel.onRetryClicked()

        val currentState = viewModel.uiState.value
        assertThat(currentState).isInstanceOf(TopUsersUiState.Success::class.java)
        assertThat((currentState as TopUsersUiState.Success).users).isEqualTo(expectedUsers)
        verify(getUsersUseCase, times(2)).invoke()
    }

    @Test
    fun `when onFollowClicked is called, followUserUseCase is invoked`() = runTest {
        whenever(getUsersUseCase()).thenReturn(flowOf(emptyList()))
        val viewModel = createViewModel()
        val accountId = 1

        viewModel.onFollowClicked(accountId)

        verify(followUserUseCase).invoke(accountId)
    }

    @Test
    fun `when onUnfollowClicked is called, unfollowUserUseCase is invoked`() = runTest {
        whenever(getUsersUseCase()).thenReturn(flowOf(emptyList()))
        val viewModel = createViewModel()
        val accountId = 123

        viewModel.onUnfollowClicked(accountId)

        verify(unfollowUserUseCase).invoke(accountId)
    }

    private fun createViewModel() = TopStackoverflowUsersViewModel(
        getTopUsersUseCase = getUsersUseCase,
        followUserUseCase = followUserUseCase,
        unfollowUserUseCase = unfollowUserUseCase
    )
}