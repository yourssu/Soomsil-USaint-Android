package com.yourssu.soomsil.usaint

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class StudentCredential(
    val id: String? = null,
    val pw: String? = null,
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    var studentCredential: StudentCredential? by mutableStateOf(null)

    init {
        viewModelScope.launch {
            studentCredential = dataStore.data.map { pref ->
                val id = pref[PreferencesKeys.STUDENT_ID]
                val pw = pref[PreferencesKeys.STUDENT_PW]
                StudentCredential(id, pw)
            }.first()
        }
    }
}