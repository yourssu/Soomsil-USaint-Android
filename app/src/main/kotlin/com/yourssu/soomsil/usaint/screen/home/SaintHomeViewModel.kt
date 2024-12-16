package com.yourssu.soomsil.saint.screen.home

import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.yourssu.soomsil.saint.util.USER_INFO
import com.yourssu.soomsil.saint.util.USER_NAME
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SaintHomeViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences,
) : ViewModel() {
    var userName: String? by mutableStateOf(null)
        private set
    var userInfo: String? by mutableStateOf(null)
        private set

    init {
        update()
    }

    fun update() {
        userName = sharedPreferences.getString(USER_NAME, null)?.takeIf { it.isNotEmpty() }
        userInfo = sharedPreferences.getString(USER_INFO, null)?.takeIf { it.isNotEmpty() }
    }
}
