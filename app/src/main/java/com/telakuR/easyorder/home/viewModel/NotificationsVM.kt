package com.telakuR.easyorder.home.viewModel

import com.telakuR.easyorder.mainRepository.NotificationsRepository
import com.telakuR.easyorder.mainViewModel.EasyOrderViewModel
import com.telakuR.easyorder.models.NotificationModel
import com.telakuR.easyorder.services.LogService
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

    fun getNotifications() {
        launchCatching {
            notificationsRepository.getNotifications().collect {
                _notifications.value = it
            }
        }
    }
}