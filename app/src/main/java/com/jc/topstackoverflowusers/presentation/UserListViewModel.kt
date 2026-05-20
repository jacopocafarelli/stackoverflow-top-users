package com.jc.topstackoverflowusers.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jc.topstackoverflowusers.domain.usecase.GetTopUsersUseCase
import com.jc.topstackoverflowusers.presentation.mapper.toErrorType
import com.jc.topstackoverflowusers.presentation.model.TopUsersUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TopStackoverflowUsersViewModel @Inject constructor(
    private val getTopUsersUseCase: GetTopUsersUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<TopUsersUiState>(TopUsersUiState.Loading)
    val uiState: StateFlow<TopUsersUiState> = _uiState.asStateFlow()

    init {
        loadUsers()
    }

    private fun loadUsers() {
        viewModelScope.launch {
            _uiState.update { TopUsersUiState.Loading }

            val result = getTopUsersUseCase()
            result.fold(
                onSuccess = { users ->
                    _uiState.update { TopUsersUiState.Success(users) }
                },
                onFailure = { exception ->
                    val errorType = exception.toErrorType()
                    _uiState.update { TopUsersUiState.Error(errorType) }
                }
            )
        }
    }

    fun onRetryClicked() {
        loadUsers()
    }
}