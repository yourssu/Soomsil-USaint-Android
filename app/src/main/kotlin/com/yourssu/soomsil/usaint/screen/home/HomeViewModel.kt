package com.yourssu.soomsil.usaint.screen.home

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : ViewModel() {

    init {
        getStudentInfo()
    }

    fun getStudentInfo() {
        viewModelScope.launch {
//            StudentInformationApplicationBuilder().build()
        }
    }
}