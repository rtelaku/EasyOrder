package com.telakuR.easyorder.home.ui.screens

import BottomRightButton
import PartOfNoCompanyView
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.telakuR.easyorder.R
import com.telakuR.easyorder.enums.RolesEnum
import com.telakuR.easyorder.home.models.OrderDetails
import com.telakuR.easyorder.home.route.HomeRoute
import com.telakuR.easyorder.home.viewModel.HomeVM
import com.telakuR.easyorder.models.User
import com.telakuR.easyorder.ui.theme.*
import java.util.*

@Composable
fun UserHomeScreen(navController: NavHostController, viewModel: HomeVM = hiltViewModel(), role: String) {
    Scaffold(content = { it
        if(role == RolesEnum.COMPANY.role) {
            CompanyHome(viewModel)
        } else if(role == RolesEnum.USER.role) {
            EmployeeHome(viewModel, navController = navController)
        }
    },
    backgroundColor = Background)
}

@Composable
private fun EmployeeHome(viewModel: HomeVM, navController: NavHostController) {
    viewModel.isUserInACompany()
    val isUserInACompany = viewModel.isUserOnACompany.collectAsState().value

    Column(modifier = Modifier.fillMaxSize()) {
        if (isUserInACompany == false) {
            PartOfNoCompanyView()
        } else if (isUserInACompany == true) {

            viewModel.getListOfOrders()

            val textState = remember { mutableStateOf(TextFieldValue("")) }

            val orders = viewModel.orders.collectAsState().value

            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Top) {
                Text(
                    text = stringResource(id = R.string.orders),
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
                    NoItemsText(textId = R.string.no_orders)
                } else {
                    OrderList(orders = orders, state = textState, navController = navController)
                }
            }

            BottomRightButton(textId = R.string.create_order) {
                navController.navigate(HomeRoute.ChooseFastFood.route)
            }
        }
    }
}

@Composable
private fun OrderList(orders: List<OrderDetails>, state: MutableState<TextFieldValue>, navController: NavHostController) {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(orders) { order ->
            OrderItem(order = order, navController = navController)
        }
    }
}

@Composable
private fun OrderItem(order: OrderDetails, navController: NavHostController) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            painter = painterResource(R.drawable.order_background),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth()
        )

        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, start = 70.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.Start) {
            Text(text = "${order.owner}:", color = Color.White, fontSize = 15.sp)
            Text(text = order.fastFood, color = Color.White, fontSize = 15.sp)

            ItemButton(
                textId = R.string.order_now,
                backgroundColor = Color.White,
                corners = 6,
                contentColor = PrimaryColor,
                padding = 0
            ) {
                navController.navigate(HomeRoute.ChooseFood.route)
            }
        }

    }

    Spacer(modifier = Modifier.height(20.dp))
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun CompanyHome(viewModel: HomeVM) {
    Column(modifier = Modifier.fillMaxSize()) {

        viewModel.getListOfEmployees()

        val textState = remember { mutableStateOf(TextFieldValue("")) }

        val employees = viewModel.employees.collectAsState().value

        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Top) {
            Text(
                text = stringResource(id = R.string.list_of_employees),
                modifier = Modifier.padding(start = 10.dp),
                fontSize = 25.sp
            )

            val employeesByName = employees.map { it.name }
            SearchBar(searchTextId = R.string.search_employee, items = employeesByName, textState = textState)
        }

        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Center) {
            if(employees.isEmpty()) {
                NoItemsText(textId = R.string.no_employees)
            } else {
                EmployeesList(employees = employees, state = textState, viewModel = viewModel)
            }
        }
    }
}

@Composable
private fun EmployeesList(
    employees: List<User>,
    state: MutableState<TextFieldValue>,
    viewModel: HomeVM
) {
    var filteredEmployees: List<User>
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        val searchedText = state.value.text
        filteredEmployees = if (searchedText.isEmpty()) {
            employees
        } else {
            val resultList = ArrayList<User>()
            for (employee in employees) {
                if (employee.name.lowercase(Locale.getDefault())
                        .contains(searchedText.lowercase(Locale.getDefault()))
                ) {
                    resultList.add(employee)
                }
            }
            resultList
        }
        items(filteredEmployees) { filteredEmployee ->
            EmployeeItem(employee = filteredEmployee, viewModel = viewModel)
        }
    }
}

@Composable
private fun EmployeeItem(employee: User, viewModel: HomeVM) {
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
                Column {
                    AsyncRoundedImage(
                        image = employee.profilePic,
                        size = 65,
                        cornerSize = 10
                    )
                }

                Column(
                    modifier = Modifier
                        .padding(start = 10.dp)
                ) {
                    Text(text = employee.name)
                }
            }

            Row(horizontalArrangement = Arrangement.End) {
                ItemButton(
                    textId = R.string.remove,
                    backgroundColor = Color.Red
                ) {
                    viewModel.removeEmployee(id = employee.id)
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(10.dp))
}