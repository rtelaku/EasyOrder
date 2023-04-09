package com.telakuR.easyorder.home.ui.screens.employeeView

import android.widget.Toast
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.telakuR.easyorder.R
import com.telakuR.easyorder.home.models.MenuItem
import com.telakuR.easyorder.home.route.HomeRoute
import com.telakuR.easyorder.home.viewModel.OrdersVM
import com.telakuR.easyorder.ui.theme.*
import com.telakuR.easyorder.utils.ToastUtils
import java.util.*

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ChooseFoodScreen(
    fastFoodName: String,
    navController: NavController,
    viewModel: OrdersVM = hiltViewModel(),
    orderId: String?
) {
    fastFoodName.ifEmpty { viewModel.getFastFoodByOrderId(orderId = orderId) }
    val fastFood = viewModel.fastFoodName.collectAsStateWithLifecycle().value

    viewModel.getMenuItems(fastFood.ifEmpty { fastFoodName })
    val menuList = viewModel.fastFoodMenu.collectAsStateWithLifecycle().value

    Scaffold(
        topBar = {
            TopAppBar()
        },
        content = {
            it
            Column(modifier = Modifier.fillMaxSize()) {
                val textState = remember { mutableStateOf(TextFieldValue("")) }

                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Top) {
                    val menuItemByName = menuList.map { it.name }
                    SearchBar(
                        searchTextId = R.string.search_food,
                        items = menuItemByName,
                        textState = textState
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center
                ) {
                   MenuItemsList(menu = menuList, textState = textState, viewModel = viewModel, fastFoodName = fastFoodName, orderId = orderId ?: "")
                }
            }
        },
        backgroundColor = Background
    )

    val context = LocalContext.current
    val continueWithOrder = viewModel.continueOrder.collectAsState().value

    LaunchedEffect(continueWithOrder) {
        if (continueWithOrder != null) {
            if(continueWithOrder) {
                navController.navigate(HomeRoute.Orders.route)
            } else {
                ToastUtils.showToast(
                    context = context,
                    messageId = R.string.something_went_wrong,
                    length = Toast.LENGTH_SHORT
                )
            }
        }
    }
}

@Composable
private fun TopAppBar() {
    Column(modifier = Modifier.fillMaxWidth(0.5f)) {
        Text(
            text = stringResource(R.string.select_favorite_food),
            modifier = Modifier
                .padding(start = 10.dp),
            fontSize = 25.sp
        )
    }
}

@Composable
private fun MenuItemsList(menu: List<MenuItem>, textState: MutableState<TextFieldValue>, viewModel: OrdersVM, fastFoodName: String, orderId: String) {
    var filteredMenu: List<MenuItem>

    LazyColumn {
        val searchedText = textState.value.text

        filteredMenu = if (searchedText.isEmpty()) {
            menu
        } else {
            val resultList = ArrayList<MenuItem>()
            for (item in menu) {
                if (item.name.lowercase(Locale.getDefault())
                        .contains(searchedText.lowercase(Locale.getDefault()))
                ) {
                    resultList.add(item)
                }
            }
            resultList
        }

        items(filteredMenu) { item ->
            FoodMenuItem(item = item, viewModel = viewModel, fastFoodName = fastFoodName, orderId = orderId)
        }
    }
}

@Composable
private fun FoodMenuItem(item: MenuItem, viewModel: OrdersVM, fastFoodName: String, orderId: String) {
    WhiteItemCard(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp)
        .clickable {
            if (orderId.isBlank()) {
                viewModel.createOrder(fastFood = fastFoodName, menuItem = item)
            } else {
                viewModel.addMenuItem(menuItem = item, orderId = orderId)
            }
        }) {
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
                        image = item.picture,
                        size = 65,
                        cornerSize = 10
                    )
                }

                Column(
                    modifier = Modifier
                        .padding(start = 10.dp)
                ) {
                    Text(text = item.name)
                }
            }

            Row(horizontalArrangement = Arrangement.End) {
                Text(text = "${item.price}€", color = OrangeTextColor, fontSize = 18.sp)
            }
        }
    }

    Spacer(modifier = Modifier.height(10.dp))
}
