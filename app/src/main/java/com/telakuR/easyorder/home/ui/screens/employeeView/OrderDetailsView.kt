package com.telakuR.easyorder.home.ui.screens.employeeView

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.telakuR.easyorder.R
import com.telakuR.easyorder.home.models.EmployeeMenuItem
import com.telakuR.easyorder.home.route.HomeRoute
import com.telakuR.easyorder.home.viewModel.MyOrdersVM
import com.telakuR.easyorder.main.ui.theme.*
import com.telakuR.easyorder.utils.Constants.ORDER_ID

@Composable
fun OrderDetailsScreen(
    navController: NavController,
    viewModel: MyOrdersVM = hiltViewModel(),
    employeeId: String,
    orderId: String
) {
    val isMyOrder = viewModel.isMyOrder(employeeId)
    val dialogState: MutableState<Pair<Boolean, EmployeeMenuItem?>> = remember { mutableStateOf(Pair(false, null)) }

    Scaffold(
        content = { it
            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Top) {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Top) {
                    Text(
                        text = stringResource(id = R.string.ordered),
                        modifier = Modifier.padding(start = 10.dp),
                        fontSize = 25.sp
                    )
                }

                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Center) {
                    MyOrderMenuDetails(
                        orderId = orderId,
                        viewModel = viewModel,
                        dialogState = dialogState,
                        isMyOrder = isMyOrder
                    )

                    RemoveMenuItemDialog(dialogState = dialogState, viewModel = viewModel, orderId = orderId)
                }
            }
        },
        bottomBar = {
            OrderDetailsBottomBar(isMyOrder = isMyOrder, navController = navController, orderId = orderId)
        },
        backgroundColor = Background
    )
}

@Composable
private fun MyOrderMenuDetails(
    orderId: String,
    viewModel: MyOrdersVM,
    dialogState: MutableState<Pair<Boolean, EmployeeMenuItem?>>,
    isMyOrder: Boolean
) {
    viewModel.getMyOrderMenu(orderId = orderId, isMyOrder = isMyOrder)
    val myOrderMenu = viewModel.myOrderMenu.collectAsStateWithLifecycle().value

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp), verticalArrangement = Arrangement.Center
    ) {
        MyOrderList(myOrderMenu = myOrderMenu, dialogState = dialogState)
    }
}

@Composable
private fun MyOrderList(
    myOrderMenu: List<EmployeeMenuItem>,
    dialogState: MutableState<Pair<Boolean, EmployeeMenuItem?>>
) {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(myOrderMenu) { menuItem ->
            OrderMenuItem(menuItem = menuItem, dialogState = dialogState)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OrderMenuItem(menuItem: EmployeeMenuItem, dialogState: MutableState<Pair<Boolean, EmployeeMenuItem?>>) {
    WhiteItemCard(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp)
        .combinedClickable(
            onClick = {},
            onLongClick = {
                dialogState.value = Pair(true, menuItem)
            }
        )) {
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
                        image = menuItem.userInfo.employeePicture,
                        size = 65,
                        cornerSize = 10
                    )
                }

                Column(
                    modifier = Modifier
                        .padding(start = 10.dp)
                ) {
                    Text(text = menuItem.userInfo.employeeName, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(text = menuItem.menuItem.menuName, color = Color.LightGray)
                }
            }

            Row(horizontalArrangement = Arrangement.End) {
                Text(text = menuItem.menuItem.price.toString() + "€", color = Color.LightGray)
            }
        }
    }

    Spacer(modifier = Modifier.height(10.dp))
}

@Composable
private fun OrderDetailsBottomBar(isMyOrder: Boolean, navController: NavController, orderId: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {

        if(isMyOrder) {
            MainButton(
                textId = R.string.grouped_orders,
                alignment = Alignment.Start,
                modifier = Modifier.weight(2f)
            ) {
                navController.navigate(HomeRoute.GroupedOrders.route + "/$orderId")
            }

            MainButton(
                textId = R.string.payment_details,
                alignment = Alignment.End,
                modifier = Modifier.weight(2f)
            ) {
                navController.navigate(HomeRoute.PaymentDetails.route + "/$orderId")
            }
        }

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.BottomEnd
        ) {
            OutlinedButton(
                onClick = {
                    navController.navigate(HomeRoute.ChooseFood.route + "/?$ORDER_ID=${orderId}")
                },
                modifier = Modifier
                    .height(70.dp)
                    .padding(5.dp),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    backgroundColor = PrimaryColor,
                    contentColor = Color.White
                )
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        }
    }
}

@Composable
private fun RemoveMenuItemDialog(
    dialogState: MutableState<Pair<Boolean, EmployeeMenuItem?>>,
    viewModel: MyOrdersVM,
    orderId: String
) {
    val menuItem = dialogState.value.second

    if (dialogState.value.first) {
        Dialog(onDismissRequest = { dialogState.value = Pair(false, null) }) {
            WhiteItemCard(modifier = Modifier.padding(16.dp)) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(text = stringResource(id = R.string.are_you_sure_you_want_to_delete_item))

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        colors = ButtonDefaults.buttonColors(backgroundColor = PrimaryColor),
                        shape = RoundedCornerShape(15.dp),
                        onClick = {
                            if (menuItem != null) {
                                viewModel.removeMenuItem(orderId = orderId, menuItem = menuItem)
                            }
                            dialogState.value = Pair(false, null)
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
