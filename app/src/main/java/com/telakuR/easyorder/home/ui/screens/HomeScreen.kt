package com.telakuR.easyorder.home.ui.screens

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.telakuR.easyorder.enums.RolesEnum
import com.telakuR.easyorder.models.User
import com.telakuR.easyorder.ui.theme.*
import com.telakuR.easyorder.viewModels.HomeVM
import java.util.*

@Composable
fun HomeScreen(viewModel: HomeVM = hiltViewModel()) {
    Scaffold(content = { it
        val role = RolesEnum.COMPANY.role

        if(role == RolesEnum.COMPANY.role) {
            CompanyHome(viewModel)
        } else if(role == RolesEnum.USER.role) {
            UserHome()
        }
    },
    backgroundColor = Background)
}

@Composable
fun UserHome() {

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
                text = "List of employees",
                modifier = Modifier.padding(start = 10.dp),
                fontSize = 25.sp
            )

            val employeesByName = employees.map { it.name }
            SearchBar(searchText = "Search for employee", items = employeesByName, textState = textState)
        }

        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Center) {
            if(employees.isEmpty()) {
                NoItemsText("No employees")
            } else {
                EmployeesList(employees = employees, state = textState, viewModel = viewModel)
            }
        }
    }
}

@Composable
fun NoItemsText(text: String) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(text = text, fontSize = 18.sp)
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
                    text = "Remove",
                    backgroundColor = Color.Red
                ) {
                    viewModel.removeEmployee(email = employee.email)
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(10.dp))
}