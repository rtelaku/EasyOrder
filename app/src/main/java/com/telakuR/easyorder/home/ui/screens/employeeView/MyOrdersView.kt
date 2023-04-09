package com.telakuR.easyorder.home.ui.screens.employeeView

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.telakuR.easyorder.R
import com.telakuR.easyorder.home.models.OrderDetails
import com.telakuR.easyorder.home.route.HomeRoute
import com.telakuR.easyorder.home.viewModel.OrdersVM
import com.telakuR.easyorder.ui.theme.*

@Composable
fun MyOrdersScreen(navController: NavHostController, viewModel: OrdersVM = hiltViewModel()) {
    viewModel.getListOfMyOrders()
    val orders = viewModel.myOrderList.collectAsStateWithLifecycle().value

    Scaffold(
        content = {
            it
            Column(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Top) {
                    Text(
                        text = stringResource(id = R.string.my_orders),
                        modifier = Modifier.padding(start = 10.dp),
                        fontSize = 25.sp
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp), verticalArrangement = Arrangement.Center
                ) {
                    if (orders.isEmpty()) {
                        NoItemsText(textId = R.string.no_order)
                    } else {
                        OrderList(orders = orders, navController = navController, viewModel = viewModel)
                    }
                }
            }
        },
        backgroundColor = Background
    )
}

@Composable
private fun OrderList(orders: List<OrderDetails>, navController: NavController, viewModel: OrdersVM) {
    val myId = viewModel.getMyId()

    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(orders) { order ->
            val isMyOrder = order.employeeId == myId

            if(isMyOrder) {
                MyOrderItem(order = order, navController = navController, viewModel = viewModel)
            } else {
                OrderItem(order = order, navController = navController, viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MyOrderItem(order: OrderDetails, navController: NavController, viewModel: OrdersVM) {
    RoundedItemCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .combinedClickable(
                onClick = {},
                onLongClick = {
                    viewModel.completeOrder(orderId = order.id)
                }
            ),
        backgroundColor = PrimaryColor
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
                Column(
                    modifier = Modifier
                        .padding(start = 10.dp)
                ) {
                    Text(text = order.fastFood, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    Text(text = stringResource(id = R.string.my_order), color = Color.White)
                }
            }

            Row(
                modifier = Modifier.clickable {
                    navController.navigate(HomeRoute.OrderDetails.route + "/${order.id}/${order.employeeId}")
                },
                horizontalArrangement = Arrangement.End
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = "Forward",
                    tint = Color.White
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(5.dp))
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OrderItem(order: OrderDetails, navController: NavController, viewModel: OrdersVM) {
    WhiteItemCard(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp)) {
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
                Column(
                    modifier = Modifier
                        .padding(start = 10.dp)
                ) {
                    Text(text = order.fastFood, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = PrimaryColor)
                    Text(text = order.owner, color = PrimaryColor)
                }
            }

            Row(
                modifier = Modifier.clickable {
                    navController.navigate(HomeRoute.OrderDetails.route + "/${order.id}/${order.employeeId}")
                },
                horizontalArrangement = Arrangement.End
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = "Forward",
                    tint = PrimaryColor
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(5.dp))
}
