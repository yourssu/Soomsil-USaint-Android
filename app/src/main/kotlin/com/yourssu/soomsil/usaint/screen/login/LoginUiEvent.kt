package com.yourssu.soomsil.usaint.screen.login

sealed interface LoginUiEvent {
    data object Loading : LoginUiEvent
    data object Success : LoginUiEvent
    data class Error(val msg: String) : LoginUiEvent
}