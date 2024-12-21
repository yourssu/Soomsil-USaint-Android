package com.yourssu.soomsil.usaint.screen

/**
 * 각 Screen에 공통적인 이벤트 타입입니다.
 * 이벤트 타입을 추가하고 싶다면 UiEvent를 상속받아 사용하세요.
 */
interface UiEvent {
    data object Success : UiEvent
    data class Failure(val msg: String? = null) : UiEvent
    data object SessionFailure : UiEvent
    data object RefreshFailure : UiEvent
}