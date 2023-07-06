package com.telakuR.easyorder.home.ui.screens.employeeView

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.telakuR.easyorder.R
import com.telakuR.easyorder.home.models.OrderDetails
import com.telakuR.easyorder.home.route.HomeRoute
import com.telakuR.easyorder.home.viewModel.MyOrdersVM
import com.telakuR.easyorder.main.ui.theme.*

@Composable
fun MyOrdersScreen(navController: NavHostController, viewModel: MyOrdersVM = hiltViewModel()) {
    viewModel.getListOfMyOrders()
    val orders = viewModel.myOrderList.collectAsStateWithLifecycle().value
    val dialogState: MutableState<Triple<Boolean, Boolean, String>> = remember { mutableStateOf(Triple(false, false, "")) }

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
                        OrderList(orders = orders, navController = navController, viewModel = viewModel, dialogState = dialogState)
                    }
                }

                CompleteOrderDialog(dialogState = dialogState, viewModel = viewModel)
            }
        },
        backgroundColor = Background
    )
}

@Composable
private fun OrderList(
    orders: List<OrderDetails>,
    navController: NavController,
    viewModel: MyOrdersVM,
    dialogState: MutableState<Triple<Boolean, Boolean, String>>
) {
    val myId = viewModel.getMyId()
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(orders) { order ->
            MyOrderItem(order = order, navController = navController, dialogState = dialogState, myId = myId)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MyOrderItem(
    order: OrderDetails,
    navController: NavController,
    dialogState: MutableState<Triple<Boolean, Boolean, String>>,
    myId: String
) {
    val isMyOrder = order.employeeId == myId
    val textColor = if(isMyOrder) Color.White else PrimaryColor
    val backgroundColor = if(isMyOrder) PrimaryColor else Color.White
    val buttonColor = if(isMyOrder) Color.White else PrimaryColor

    RoundedItemCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .combinedClickable(
                onClick = {},
                onLongClick = {
                    dialogState.value = Triple(true, isMyOrder, order.id)
                }
            ),
        backgroundColor = backgroundColor
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
                    Text(text = order.fastFood, color = textColor, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    val orderOwnerText = if(isMyOrder) stringResource(id = R.string.my_order) else order.owner
                    Text(text = orderOwnerText, color = textColor)
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
                    tint = buttonColor
                )
            }
        }
    }
}

@Composable
private fun CompleteOrderDialog(dialogState: MutableState<Triple<Boolean, Boolean, String>>, viewModel: MyOrdersVM) {
    val isMyOrder = dialogState.value.second
    val orderId = dialogState.value.third

    if (dialogState.value.first) {
        Dialog(onDismissRequest = { dialogState.value = Triple(false, false, "") }) {
            WhiteItemCard(modifier = Modifier.padding(16.dp)) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    val messageId = if(isMyOrder) R.string.are_you_sure_you_want_to_complete_order else R.string.are_you_sure_you_want_to_remove_order
                    Text(text = stringResource(id = messageId))

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        colors = ButtonDefaults.buttonColors(backgroundColor = PrimaryColor),
                        shape = RoundedCornerShape(15.dp),
                        onClick = {
                            if(isMyOrder) {
                                viewModel.completeOrder(orderId = orderId)
                            } else {
                                viewModel.removeOrder(orderId = orderId)
                            }
                            dialogState.value = Triple(false, false, "")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(stringResource(id = R.string.confirm), color = Color.White)
                    }
                }
            }
        }
    }
}
