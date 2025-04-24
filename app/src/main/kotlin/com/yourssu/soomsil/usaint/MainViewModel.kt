package com.yourssu.soomsil.usaint

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourssu.soomsil.usaint.data.repository.StudentCredentialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    studentCredentialRepository: StudentCredentialRepository,
) : ViewModel() {
    private val _mainUiState = MutableStateFlow<MainUiState>(MainUiState.Loading)
    val mainUiState = _mainUiState.asStateFlow()

    init {
        viewModelScope.launch {
            studentCredentialRepository.getStudentCredential().let { (id, password) ->
                _mainUiState.value = if (id.isNotEmpty() && password.isNotEmpty()) {
                    MainUiState.Success
                } else {
                    MainUiState.InvalidOrNoCredential
                }
            }
        }
    }
}

sealed interface MainUiState {
    data object Loading : MainUiState
    data object Success : MainUiState
    data object InvalidOrNoCredential : MainUiState
}