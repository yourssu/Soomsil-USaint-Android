package com.yourssu.soomsil.usaint.screen.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourssu.soomsil.usaint.data.repository.StudentInfoRepository
import com.yourssu.soomsil.usaint.data.repository.TotalReportCardRepository
import com.yourssu.soomsil.usaint.data.repository.USaintSessionRepository
import com.yourssu.soomsil.usaint.screen.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.eatsteak.rusaint.ffi.RusaintException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val uSaintSessionRepo: USaintSessionRepository,
    private val studentInfoRepo: StudentInfoRepository,
    private val totalReportCardRepo: TotalReportCardRepository,
) : ViewModel() {
    private val _uiEvent: MutableSharedFlow<UiEvent> = MutableSharedFlow()
    val uiEvent = _uiEvent.asSharedFlow()

    var isLoading: Boolean by mutableStateOf(false)
        private set
    var studentId: String by mutableStateOf("")
    var studentPw: String by mutableStateOf("")

    fun login() {
        val id = studentId
        val pw = studentPw

        viewModelScope.launch {
            isLoading = true
            // 로그인 시도
            // 실패 시 Error 이벤트 발생 후 종료
            val session = uSaintSessionRepo.withPassword(id, pw).getOrElse { e ->
                Timber.e(e)
                when (e) {
                    is RusaintException -> _uiEvent.emit(UiEvent.SessionFailure)
                    else -> _uiEvent.emit(UiEvent.Failure())
                }
                isLoading = false
                return@launch
            }
            val studentInfoVO = studentInfoRepo.getRemoteStudentInfo(session).getOrElse { e ->
                Timber.e(e)
                _uiEvent.emit(UiEvent.Failure("학생 정보를 가져오는 데 실패했습니다."))
                isLoading = false
                return@launch
            }
            val totalReportCard = totalReportCardRepo.getRemoteReportCard(session).getOrElse { e ->
                Timber.e(e)
                _uiEvent.emit(UiEvent.Failure("증명 평점 정보를 가져오는 데 실패했습니다."))
                isLoading = false
                return@launch
            }
            // 성공 시 id/pw, 학생 정보 저장
            studentInfoRepo.storePassword(id, pw).onFailure { e -> Timber.e(e) }
            studentInfoRepo.storeStudentInfo(studentInfoVO).onFailure { e -> Timber.e(e) }
            totalReportCardRepo.storeReportCard(totalReportCard).onFailure { e -> Timber.e(e) }
            _uiEvent.emit(UiEvent.Success)
            isLoading = false
        }
    }
}