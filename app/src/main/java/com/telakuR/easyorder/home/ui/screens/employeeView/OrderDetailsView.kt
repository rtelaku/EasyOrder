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
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.telakuR.easyorder.R
import com.telakuR.easyorder.home.models.EmployeeMenuItem
import com.telakuR.easyorder.home.navigation.ORDER_ID
import com.telakuR.easyorder.home.route.HomeRoute
import com.telakuR.easyorder.home.viewModel.OrdersVM
import com.telakuR.easyorder.setupProfile.route.SetUpProfileRoute
import com.telakuR.easyorder.ui.theme.*

@Composable
fun OrderDetailsScreen(
    navController: NavController,
    viewModel: OrdersVM = hiltViewModel(),
    employeeId: String,
    orderId: String
) {
    val isMyOrder = viewModel.isMyOrder(employeeId)
    val dialogState: MutableState<Pair<Boolean, String>> = remember { mutableStateOf(Pair(false, "")) }

    Scaffold(
        content = {
            it
            if (isMyOrder) {
                MyOrderMenuDetails(
                    orderId = orderId,
                    viewModel = viewModel,
                    dialogState = dialogState
                )
            } else {
                OtherOrderMenuDetails(
                    orderId = orderId,
                    viewModel = viewModel,
                    dialogState = dialogState
                )
            }

            RemoveMenuItemDialog(dialogState = dialogState, viewModel = viewModel, orderId = orderId)
        },
        bottomBar = {
            BottomBar(isMyOrder = isMyOrder, navController = navController, orderId = orderId)
        },
        backgroundColor = Background
    )
}

@Composable
fun MyOrderMenuDetails(
    orderId: String,
    viewModel: OrdersVM,
    dialogState: MutableState<Pair<Boolean, String>>
) {
    viewModel.getMyOrderMenu(orderId = orderId)
    val myOrderMenu = viewModel.myOrderMenu.collectAsStateWithLifecycle().value

    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp), verticalArrangement = Arrangement.Center) {
            MyOrderList(myOrderMenu = myOrderMenu, dialogState = dialogState)
        }
    }
}

@Composable
private fun MyOrderList(
    myOrderMenu: List<EmployeeMenuItem>,
    dialogState: MutableState<Pair<Boolean, String>>
) {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(myOrderMenu) { menuItem ->
            OrderMenuItem(menuItem = menuItem, dialogState = dialogState)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OrderMenuItem(menuItem: EmployeeMenuItem, dialogState: MutableState<Pair<Boolean, String>>) {
    WhiteItemCard(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp)
        .combinedClickable(
            onClick = {},
            onLongClick = {
                dialogState.value = Pair(true, menuItem.menuItem.name)
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
                        image = menuItem.userInfo.picture,
                        size = 65,
                        cornerSize = 10
                    )
                }

                Column(
                    modifier = Modifier
                        .padding(start = 10.dp)
                ) {
                    Text(text = menuItem.userInfo.name, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(text = menuItem.menuItem.name, color = Color.LightGray)
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
fun OtherOrderMenuDetails(orderId: String, viewModel: OrdersVM, dialogState: MutableState<Pair<Boolean, String>>) {
    viewModel.getOtherOrder(orderId = orderId)
    val myOrderMenu = viewModel.myOrderMenu.collectAsStateWithLifecycle().value

    Scaffold(
        content = {
            it
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp), verticalArrangement = Arrangement.Center) {
                OtherOrderList(myOrderMenu = myOrderMenu, dialogState = dialogState)
            }
        },
        backgroundColor = Background
    )
}

@Composable
private fun OtherOrderList(
    myOrderMenu: List<EmployeeMenuItem>,
    dialogState: MutableState<Pair<Boolean, String>>
) {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(myOrderMenu) { menuItem ->
            OrderMenuItem(menuItem = menuItem, dialogState = dialogState)
        }
    }
}

@Composable
fun BottomBar(isMyOrder: Boolean, navController: NavController, orderId: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {

        if(isMyOrder) {
            MainButton(
                textId = R.string.grouped_orders,
                alignment = Alignment.Start,
                modifier = Modifier.weight(2f)
            ) {
                navController.navigate(SetUpProfileRoute.PicturePreview.route)
            }

            MainButton(
                textId = R.string.payment_details,
                alignment = Alignment.End,
                modifier = Modifier.weight(2f)
            ) {
                navController.navigate(SetUpProfileRoute.PicturePreview.route)
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
    dialogState: MutableState<Pair<Boolean, String>>,
    viewModel: OrdersVM,
    orderId: String
) {
    val menuItemName = dialogState.value.second

    if (dialogState.value.first) {
        Dialog(onDismissRequest = { dialogState.value = Pair(false, "") }) {
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
                            viewModel.removeMenuItem(orderId = orderId, menuItemName = menuItemName)
                            dialogState.value = Pair(false, "")
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
