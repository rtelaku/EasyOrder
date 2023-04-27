package com.telakuR.easyorder.home.viewModel

import com.telakuR.easyorder.main.models.NotificationModel
import com.telakuR.easyorder.main.repository.NotificationsRepository
import com.telakuR.easyorder.main.services.LogService
import com.telakuR.easyorder.main.viewmodel.EasyOrderViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class NotificationsVM @Inject constructor(
    private val notificationsRepository: NotificationsRepository,
    logService: LogService
) : EasyOrderViewModel(logService) {

    private val _notifications = MutableStateFlow<List<NotificationModel>>(emptyList())
    var notifications: StateFlow<List<NotificationModel>> = _notifications

    fun getNotificationsFromDB() {
        launchCatching {
            notificationsRepository.getNotificationsFromAPI().collect { notifications ->
                notificationsRepository.saveNotificationsOnDB(notifications = notifications)
            }
        }
    }

    fun getNotificationsFromAPI() {
        launchCatching {
            notificationsRepository.getNotificationsFromDB().collect { notifications ->
                _notifications.value = notifications
            }
        }
    }
}