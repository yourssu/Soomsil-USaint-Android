package com.yourssu.soomsil.usaint.screen.semesterdetail

sealed interface GradeListUiEvent {
    data class Loading(val semesterName: String) : GradeListUiEvent
    data class Success(val msg: String) : GradeListUiEvent
    data class Error(val msg: String) : GradeListUiEvent
}

sealed interface CaptureFlag {
    data object None : CaptureFlag
    data object Original : CaptureFlag
    data object HidingInfo : CaptureFlag
}
