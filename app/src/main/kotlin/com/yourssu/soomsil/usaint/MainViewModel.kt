package com.yourssu.soomsil.usaint

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourssu.soomsil.usaint.data.repository.StudentCredentialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    studentCredentialRepository: StudentCredentialRepository
) : ViewModel() {
    val mainUiState: StateFlow<MainUiState> = studentCredentialRepository.studentCredential
        .map {
            if (it.id.isNotEmpty() && it.password.isNotEmpty()) {
                MainUiState.Success
            } else {
                MainUiState.InvalidOrNoCredential
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MainUiState.Loading,
        )
}

sealed interface MainUiState {
    data object Loading : MainUiState
    data object Success : MainUiState
    data object InvalidOrNoCredential : MainUiState
}