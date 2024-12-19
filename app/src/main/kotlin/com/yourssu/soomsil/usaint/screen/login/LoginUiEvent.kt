package com.yourssu.soomsil.usaint.screen.login

sealed interface LoginUiEvent {
    data object Success : LoginUiEvent
    data class Failure(val msg: String) : LoginUiEvent
}