package com.telakuR.easyorder.home.ui.screens.employeeView

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.telakuR.easyorder.R
import com.telakuR.easyorder.home.models.EmployeeMenuItem
import com.telakuR.easyorder.home.viewModel.OrdersVM
import com.telakuR.easyorder.setupProfile.route.SetUpProfileRoute
import com.telakuR.easyorder.ui.theme.Background
import com.telakuR.easyorder.ui.theme.MainButton
import com.telakuR.easyorder.ui.theme.NoItemsText
import com.telakuR.easyorder.ui.theme.WhiteItemCard

@Composable
fun MyOrderScreen(
    navController: NavController,
    viewModel: OrdersVM = hiltViewModel()
) {
    viewModel.getMyOrderMenu()
    val myOrderMenu = viewModel.myOrderMenu.collectAsState().value

    Scaffold(
        content = {
            it
            Column(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Center) {
                    if(myOrderMenu.isEmpty()) {
                        NoItemsText(textId = R.string.no_order)
                    } else {
                        MyOrderList(myOrderMenu = myOrderMenu, viewModel = viewModel)
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
                    MainButton(textId = R.string.grouped_orders) {
                        navController.navigate(SetUpProfileRoute.PicturePreview.route)
                    }

                    MainButton(textId = R.string.payment_details) {
                        navController.navigate(SetUpProfileRoute.PicturePreview.route)
                    }
                }
            }
        },
        backgroundColor = Background
    )
}

@Composable
private fun MyOrderList(
    myOrderMenu: List<EmployeeMenuItem>,
    viewModel: OrdersVM
) {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(myOrderMenu) { menuItem ->
            OrderMenuItem(menuItem = menuItem)
        }
    }
}

@Composable
private fun OrderMenuItem(menuItem: EmployeeMenuItem) {
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

//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.Start
//            ) {
//                Column {
//                    AsyncRoundedImage(
//                        image = menuItem.employeePic,
//                        size = 65,
//                        cornerSize = 10
//                    )
//                }
//
//                Column(
//                    modifier = Modifier
//                        .padding(start = 10.dp)
//                ) {
//                    Text(text = menuItem.employeeName)
//                    Text(text = menuItem.foodName)
//                }
//            }
//
//            Row(horizontalArrangement = Arrangement.End) {
//                Text(text = menuItem.price.toString())
//            }
        }
    }

    Spacer(modifier = Modifier.height(10.dp))
}
