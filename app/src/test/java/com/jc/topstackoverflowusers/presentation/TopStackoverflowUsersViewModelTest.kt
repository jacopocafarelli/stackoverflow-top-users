package com.jc.topstackoverflowusers.presentation

import com.google.common.truth.Truth.assertThat
import com.jc.topstackoverflowusers.domain.model.StackOverflowUser
import com.jc.topstackoverflowusers.domain.usecase.GetTopUsersUseCase
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

    private val useCase = mock<GetTopUsersUseCase>()

    @Test
    fun `when init block runs and usecase succeeds, state is updated to Success and contains users`() =
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
            whenever(useCase()).thenReturn(flowOf(expectedUsers))

            val viewModel = TopStackoverflowUsersViewModel(useCase)

            val currentState = viewModel.uiState.value
            assertThat(currentState).isInstanceOf(TopUsersUiState.Success::class.java)
            val successState = currentState as TopUsersUiState.Success
            assertThat(successState.users).isEqualTo(expectedUsers)
        }

    @Test
    fun `when init block runs and usecase fails with io error, state maps to network Error`() =
        runTest {
            val exceptionMessage = "Emulated exception"
            val expectedException = IOException(exceptionMessage)
            whenever(useCase()).thenReturn(flow { throw expectedException })

            val viewModel = TopStackoverflowUsersViewModel(useCase)

            val currentState = viewModel.uiState.value
            assertThat(currentState).isInstanceOf(TopUsersUiState.Error::class.java)
            val errorState = currentState as TopUsersUiState.Error
            assertThat(errorState.errorType).isEqualTo(ErrorType.NETWORK)
        }

    @Test
    fun `when init block runs and usecase fails with http error, state maps to network Error`() =
        runTest {
            val errorResponse = Response.error<Any>(
                500,
                "{\"error\": \"Generic error\"}".toResponseBody(null)
            )
            val expectedException = HttpException(errorResponse)
            whenever(useCase()).thenReturn(flow { throw expectedException })

            val viewModel = TopStackoverflowUsersViewModel(useCase)

            val currentState = viewModel.uiState.value
            assertThat(currentState).isInstanceOf(TopUsersUiState.Error::class.java)
            val errorState = currentState as TopUsersUiState.Error
            assertThat(errorState.errorType).isEqualTo(ErrorType.SERVER)
        }

    @Test
    fun `when init block runs and usecase fails with generic error, state maps to default Error`() =
        runTest {
            val expectedException = RuntimeException()
            whenever(useCase()).thenReturn(flow { throw expectedException })

            val viewModel = TopStackoverflowUsersViewModel(useCase)

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
        whenever(useCase()).thenReturn(
            flow { throw RuntimeException("Fail") },
            flowOf(expectedUsers)
        )

        val viewModel = TopStackoverflowUsersViewModel(useCase)
        assertThat(viewModel.uiState.value).isInstanceOf(TopUsersUiState.Error::class.java)

        viewModel.onRetryClicked()

        val currentState = viewModel.uiState.value
        assertThat(currentState).isInstanceOf(TopUsersUiState.Success::class.java)
        assertThat((currentState as TopUsersUiState.Success).users).isEqualTo(expectedUsers)
        verify(useCase, times(2)).invoke()
    }
}