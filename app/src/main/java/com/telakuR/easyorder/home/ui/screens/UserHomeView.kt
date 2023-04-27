package com.telakuR.easyorder.home.ui.screens

import PartOfNoCompanyView
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.telakuR.easyorder.R
import com.telakuR.easyorder.home.route.HomeRoute
import com.telakuR.easyorder.home.viewModel.HomeVM
import com.telakuR.easyorder.main.enums.RolesEnum
import com.telakuR.easyorder.main.ui.theme.*
import com.telakuR.easyorder.room_db.enitites.CompanyOrderDetails
import com.telakuR.easyorder.room_db.enitites.Employee
import com.telakuR.easyorder.utils.Constants.ORDER_ID
import java.util.*

@Composable
fun UserHomeScreen(navController: NavHostController, viewModel: HomeVM = hiltViewModel(), role: String) {
    Scaffold(content = { it
        if(role == RolesEnum.COMPANY.role) {
            CompanyHome(viewModel)
        } else if(role == RolesEnum.USER.role) {
            EmployeeHome(viewModel, navController = navController)
        }

        RequestNotificationPermission()
    },
    backgroundColor = Background)
}

@Composable
private fun RequestNotificationPermission() {
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    val context = LocalContext.current
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
        SideEffect {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

@Composable
private fun EmployeeHome(viewModel: HomeVM, navController: NavHostController) {
    viewModel.isUserInACompany()
    val isUserInACompany = viewModel.isUserOnACompany.collectAsStateWithLifecycle().value

    Column(modifier = Modifier.fillMaxSize()) {
        if (isUserInACompany == false) {
            PartOfNoCompanyView()
        } else if (isUserInACompany == true) {
            AllOrders(viewModel = viewModel, navController = navController)
        }
    }
}

@Composable
private fun AllOrders(viewModel: HomeVM, navController: NavController) {
    viewModel.getListOfOrdersFromDB()
    viewModel.getListOfOrdersFromAPI()

    val orders = viewModel.orders.collectAsStateWithLifecycle().value

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Top) {
        Text(
            text = stringResource(id = R.string.order_groups),
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
            OrderList(orders = orders, navController = navController)
        }
    }
}

@Composable
private fun OrderList(orders: List<CompanyOrderDetails>, navController: NavController) {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(orders) { order ->
            OrderItem(order = order, navController = navController)
        }
    }
}

@Composable
private fun OrderItem(order: CompanyOrderDetails, navController: NavController) {
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
            Text(text = order.owner, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Text(text = order.fastFood, color = Color.White, fontSize = 15.sp)

            ItemButton(
                textId = R.string.order_now,
                backgroundColor = Color.White,
                corners = 6,
                contentColor = PrimaryColor,
                padding = 0
            ) {
                navController.navigate(HomeRoute.ChooseFood.route + "/?$ORDER_ID=${order.id}")
            }
        }
    }

    Spacer(modifier = Modifier.height(20.dp))
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun CompanyHome(viewModel: HomeVM) {
    Column(modifier = Modifier.fillMaxSize()) {
        viewModel.getListOfEmployeesFromDB()
        viewModel.getListOfEmployeesFromAPI()

        val textState = remember { mutableStateOf(TextFieldValue("")) }

        val employees = viewModel.employees.collectAsStateWithLifecycle().value

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
    employees: List<Employee>,
    state: MutableState<TextFieldValue>,
    viewModel: HomeVM
) {
    var filteredEmployees: List<Employee>
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        val searchedText = state.value.text
        filteredEmployees = if (searchedText.isEmpty()) {
            employees
        } else {
            val resultList = ArrayList<Employee>()
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
private fun EmployeeItem(employee: Employee, viewModel: HomeVM) {
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