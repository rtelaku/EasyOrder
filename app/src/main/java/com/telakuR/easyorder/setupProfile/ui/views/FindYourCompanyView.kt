package com.telakuR.easyorder.setupProfile.ui.views

import android.widget.Toast
import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.telakuR.easyorder.R
import com.telakuR.easyorder.setupProfile.models.SetUpProfileRoute
import com.telakuR.easyorder.setupProfile.viewModels.SetUpProfileViewModel
import com.telakuR.easyorder.ui.theme.*
import com.telakuR.easyorder.utils.ToastUtils.showToast

@ExperimentalAnimationApi
@Composable
fun FindYourCompanyScreen(navController: NavController, viewModel: SetUpProfileViewModel = hiltViewModel()) {
    viewModel.getCompanies()
    val companies = viewModel.companies.collectAsState().value
    var selected by remember { mutableStateOf(false) }
    var selectedCompany by remember {
        mutableStateOf("")
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Toolbar(navController = navController)

                Text(
                    text = stringResource(R.string.find_your_company),
                    modifier = Modifier
                        .padding(start = 10.dp),
                    fontSize = 25.sp
                )

                val companiesByName = viewModel.companies.collectAsState().value.map { it.name }
                SearchBar(items = companiesByName)

            }
        },
        bottomBar = {
            val context = LocalContext.current

            Column(modifier = Modifier.fillMaxWidth()) {
                MainButton(text = stringResource(id = R.string.next)) {
                    if(selected) {
                        navController.navigate(SetUpProfileRoute.FindYourCompany.route)
                    } else {
                        showToast(context = context, message = "Please select a company", length = Toast.LENGTH_SHORT)
                    }
                }
            }
        },
        content = {
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .padding(15.dp)
            ) {
                LazyColumn {
                    items(items = companies, itemContent = { company ->
                        val color = if (selectedCompany == company.email) Background else PrimaryColor
                        val textId = if (selectedCompany == company.email) R.string.requested else R.string.request_to_join

                        WhiteItemCard(modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween) {

                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start) {
                                    Column {
                                        AsyncRoundedImage(image = company.profilePic, size = 65, cornerSize = 10)
                                    }

                                    Column(
                                        modifier = Modifier
                                            .padding(start = 10.dp)
                                    ) {
                                        Text(text = company.name)
                                    }
                                }

                                Row( horizontalArrangement = Arrangement.End) {
                                    ItemButton(text = stringResource(id = textId), backgroundColor = color) {
                                        selected = !selected
                                        selectedCompany = if (selectedCompany == company.email) "" else company.email
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                    })
                }
            }
        },
        backgroundColor = Background
    )
}


