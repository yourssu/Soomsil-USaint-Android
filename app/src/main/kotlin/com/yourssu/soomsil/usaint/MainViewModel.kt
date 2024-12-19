package com.yourssu.soomsil.usaint

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourssu.soomsil.usaint.data.repository.StudentInfoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val studentInfoRepository: StudentInfoRepository
) : ViewModel() {
    var isLoggedIn: Boolean? by mutableStateOf(null)
        private set

    init {
        viewModelScope.launch {
            isLoggedIn = studentInfoRepository.getPasswordFromDataStore().isSuccess
        }
    }
}