package com.telakuR.easyorder.home.ui.screens

import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.telakuR.easyorder.R
import com.telakuR.easyorder.enums.NotificationTypeEnum
import com.telakuR.easyorder.home.viewModel.NotificationsVM
import com.telakuR.easyorder.models.NotificationModel
import com.telakuR.easyorder.ui.theme.Background
import com.telakuR.easyorder.ui.theme.NoItemsText
import com.telakuR.easyorder.ui.theme.Toolbar
import com.telakuR.easyorder.ui.theme.WhiteItemCard
import com.telakuR.easyorder.utils.getFormattedDate

@Composable
fun NotificationsScreen(navController: NavController, viewModel: NotificationsVM = hiltViewModel()) {
    viewModel.getNotifications()
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
    val context = LocalContext.current

    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(notifications) { notification ->
            when(notification.id) {
                NotificationTypeEnum.NEW_ORDER.id -> {
                    NewOrderNotificationItem(context = context, notification = notification)
                }
            }
        }
    }
}

@Composable
fun NewOrderNotificationItem(notification: NotificationModel, context: Context) {
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
                    val newOrderMessage = String.format(stringResource(id = R.string.ordering_notification), notification.ownerName, notification.fastFood)
                    Text(text = newOrderMessage, fontSize = 18.sp)

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


