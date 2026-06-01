package com.yourssu.soomsil.usaint.screen.pushnotifications

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

enum class NotificationTab(val label: String) {
    ALL("전체"),
    ACADEMIC("학사"),
    CLASS("수업"),
    EXAM("시험"),
}

data class PushNotificationItem(
    val id: String,
    val tab: NotificationTab,
    val title: String,
    val body: String,
    val read: Boolean = false,
)

data class PushNotificationsUiState(
    val notifications: List<PushNotificationItem> = emptyList(),
    val selectedTab: NotificationTab = NotificationTab.ALL,
) {
    val visibleNotifications: List<PushNotificationItem>
        get() = if (selectedTab == NotificationTab.ALL) {
            notifications
        } else {
            notifications.filter { it.tab == selectedTab }
        }

    val unreadCount: Int
        get() = visibleNotifications.count { !it.read }
}

@HiltViewModel
class PushNotificationsViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(PushNotificationsUiState())
    val uiState: StateFlow<PushNotificationsUiState> = _uiState.asStateFlow()

    fun selectTab(tab: NotificationTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun markAsRead(id: String) {
        _uiState.update { state ->
            state.copy(
                notifications = state.notifications.map {
                    if (it.id == id) it.copy(read = true) else it
                }
            )
        }
    }

    fun markAllAsRead() {
        _uiState.update { state ->
            state.copy(notifications = state.notifications.map { it.copy(read = true) })
        }
    }
}
