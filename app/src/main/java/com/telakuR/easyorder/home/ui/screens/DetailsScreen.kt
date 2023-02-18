package com.telakuR.easyorder.home.ui.screens

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
fun ReportScreen(viewModel: HomeVM = hiltViewModel()) {
    Scaffold(
        content = {
            it
            val role = RolesEnum.COMPANY.role

            if (role == RolesEnum.COMPANY.role) {
                EmployeeRequestScreen(viewModel)
            } else if (role == RolesEnum.USER.role) {

            }
        },
        backgroundColor = Background
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun EmployeeRequestScreen(viewModel: HomeVM) {
    Column(modifier = Modifier.fillMaxSize()) {

        viewModel.getListOfRequests()

        val textState = remember { mutableStateOf(TextFieldValue("")) }

        val requests = viewModel.requests.collectAsState().value

        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Top) {
            Text(
                text = "Employee Requests",
                modifier = Modifier.padding(start = 10.dp),
                fontSize = 25.sp
            )

            val employeesByName = requests.map { it.name }
            SearchBar(searchText = "Search for request", items = employeesByName, textState = textState)
        }

        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Center) {
            if(requests.isEmpty()) {
                NoItemsText("No requests")
            } else {
                RequestList(requests = requests, state = textState, viewModel = viewModel)
            }
        }
    }
}

@Composable
private fun RequestList(
    requests: List<User>,
    state: MutableState<TextFieldValue>,
    viewModel: HomeVM
) {
    var filteredEmployees: List<User>
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        val searchedText = state.value.text
        filteredEmployees = if (searchedText.isEmpty()) {
            requests
        } else {
            val resultList = ArrayList<User>()
            for (employee in requests) {
                if (employee.name.lowercase(Locale.getDefault())
                        .contains(searchedText.lowercase(Locale.getDefault()))
                ) {
                    resultList.add(employee)
                }
            }
            resultList
        }
        items(filteredEmployees) { filteredEmployee ->
            RequestItem(employee = filteredEmployee, viewModel = viewModel)
        }
    }
}

@Composable
private fun RequestItem(employee: User, viewModel: HomeVM) {
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
                Button(
                    modifier = Modifier.padding(horizontal = 3.dp),
                    onClick = {
                        viewModel.acceptRequest(employee.email)
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Green,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Check",
                        tint = Color.White)
                }

                Button(
                    modifier = Modifier.padding(horizontal = 3.dp),
                    onClick = {
                        viewModel.removeRequest(employee.email)
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Red,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White)
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(10.dp))
}
