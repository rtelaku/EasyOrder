package com.telakuR.easyorder.home.ui.screens.employeeView

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.telakuR.easyorder.R
import com.telakuR.easyorder.home.models.MenuItem
import com.telakuR.easyorder.home.viewModel.OrdersVM
import com.telakuR.easyorder.ui.theme.AsyncRoundedImage
import com.telakuR.easyorder.ui.theme.Background
import com.telakuR.easyorder.ui.theme.OrangeTextColor
import com.telakuR.easyorder.ui.theme.WhiteItemCard

@Composable
fun GroupedOrdersScreen(
    viewModel: OrdersVM = hiltViewModel(),
    orderId: String
) {
    viewModel.getMyOrderMenu(orderId = orderId)
    val groupedOrders = viewModel.myOrderMenu.collectAsStateWithLifecycle().value
    val countedMap = groupedOrders.groupBy { it.menuItem }
        .mapValues { it.value.size }
        .toList()

    Scaffold(
        content = {
            it
            Column(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Top) {
                    Text(
                        text = stringResource(id = R.string.grouped_orders),
                        modifier = Modifier.padding(start = 10.dp),
                        fontSize = 25.sp
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp), verticalArrangement = Arrangement.Center
                ) {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(countedMap) { (menuItem, count)  ->
                            GroupedOrderItem(menuItem = menuItem, count = count)
                        }
                    }
                }
            }
        },
        backgroundColor = Background
    )
}

@Composable
private fun GroupedOrderItem(
    menuItem: MenuItem,
    count: Int
) {
    WhiteItemCard(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp)
        ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Column {
                    AsyncRoundedImage(
                        image = menuItem.picture,
                        size = 65,
                        cornerSize = 10
                    )
                }

                Column(
                    modifier = Modifier
                        .padding(start = 10.dp)
                ) {
                    Text(text = menuItem.name)
                }
            }

            Row(horizontalArrangement = Arrangement.End) {
                Text(text = count.toString(), color = OrangeTextColor)
            }
        }
    }

    Spacer(modifier = Modifier.height(10.dp))
}