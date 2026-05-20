package com.jc.topstackoverflowusers.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jc.topstackoverflowusers.domain.usecase.FollowUserUseCase
import com.jc.topstackoverflowusers.domain.usecase.GetTopUsersUseCase
import com.jc.topstackoverflowusers.domain.usecase.UnfollowUserUseCase
import com.jc.topstackoverflowusers.presentation.mapper.toErrorType
import com.jc.topstackoverflowusers.presentation.model.TopUsersUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TopStackoverflowUsersViewModel @Inject constructor(
    private val getTopUsersUseCase: GetTopUsersUseCase,
    private val followUserUseCase: FollowUserUseCase,
    private val unfollowUserUseCase: UnfollowUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<TopUsersUiState>(TopUsersUiState.Loading)
    val uiState: StateFlow<TopUsersUiState> = _uiState.asStateFlow()

    init {
        loadUsers()
    }

    private fun loadUsers() {
        viewModelScope.launch {
            _uiState.update { TopUsersUiState.Loading }

            try {
                getTopUsersUseCase().collect { users ->
                    _uiState.update { TopUsersUiState.Success(users) }
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                val errorType = e.toErrorType()
                _uiState.update { TopUsersUiState.Error(errorType) }
            }
        }
    }

    fun onRetryClicked() {
        loadUsers()
    }

    fun onFollowClicked(accountId: Int) {
        viewModelScope.launch {
            followUserUseCase(accountId)
        }
    }

    fun onUnfollowClicked(accountId: Int) {
        viewModelScope.launch {
            unfollowUserUseCase(accountId)
        }
    }
}