package com.telakuR.easyorder.home.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.telakuR.easyorder.R
import com.telakuR.easyorder.main.enums.NotificationTypeEnum
import com.telakuR.easyorder.home.viewModel.NotificationsVM
import com.telakuR.easyorder.main.models.NotificationModel
import com.telakuR.easyorder.main.ui.theme.Background
import com.telakuR.easyorder.main.ui.theme.NoItemsText
import com.telakuR.easyorder.main.ui.theme.Toolbar
import com.telakuR.easyorder.main.ui.theme.WhiteItemCard
import com.telakuR.easyorder.utils.getFormattedDate

@Composable
fun NotificationsScreen(navController: NavController, viewModel: NotificationsVM = hiltViewModel()) {
    viewModel.getNotificationsFromDB()
    viewModel.getNotificationsFromAPI()

    val notifications = viewModel.notifications.collectAsStateWithLifecycle().value

    Scaffold(
        topBar = {
            Toolbar(navController = navController)
        },
        content = {
            it
            Column(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Top) {
                    Text(
                        text = stringResource(id = R.string.notifications),
                        modifier = Modifier.padding(start = 10.dp),
                        fontSize = 25.sp
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp), verticalArrangement = Arrangement.Center
                ) {
                    if (notifications.isEmpty()) {
                        NoItemsText(textId = R.string.no_notifications)
                    } else {
                        NotificationsList(notifications = notifications)
                    }
                }
            }
        },
        backgroundColor = Background
    )
}

@Composable
fun NotificationsList(notifications: List<NotificationModel>) {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(notifications) { notification ->
            NewOrderNotificationItem(notification = notification)
        }
    }
}

@Composable
fun NewOrderNotificationItem(notification: NotificationModel) {
    WhiteItemCard(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {

                Image(
                    painter = painterResource(id = R.drawable.ic_notification),
                    contentDescription = null,
                )

                Column {
                    val message = NotificationTypeEnum.getMessageBasedOnNotification(notification)
                    Text(text = message, fontSize = 18.sp)

                    if(notification.currentTimeInMillis != null) {
                        val dateAndTime = getFormattedDate(notification.currentTimeInMillis)
                        Text(text = dateAndTime ?: "", color = Color.LightGray)
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(5.dp))
}


