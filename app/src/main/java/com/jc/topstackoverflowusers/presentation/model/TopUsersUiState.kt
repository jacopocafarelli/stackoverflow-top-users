package com.jc.topstackoverflowusers.presentation.model

import com.jc.topstackoverflowusers.domain.model.StackOverflowUser

sealed interface TopUsersUiState {
    data object Loading : TopUsersUiState

    data class Success(val users: List<StackOverflowUser>) : TopUsersUiState

    data class Error(val message: String) : TopUsersUiState
}