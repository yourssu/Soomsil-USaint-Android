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

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    var isLoggedIn: Boolean? by mutableStateOf(null)
        private set

    init {
        viewModelScope.launch {
            isLoggedIn = dataStore.data.map { pref ->
                val id = pref[PreferencesKeys.STUDENT_ID]
                val pw = pref[PreferencesKeys.STUDENT_PW]
                id != null && pw != null
            }.first()
        }
    }
}