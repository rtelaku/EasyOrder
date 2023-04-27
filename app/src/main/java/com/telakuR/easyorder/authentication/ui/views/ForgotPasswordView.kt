package com.telakuR.easyorder.authentication.ui.views

import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.telakuR.easyorder.R
import com.telakuR.easyorder.authentication.models.AuthenticationRoute
import com.telakuR.easyorder.authentication.viewmodel.LoginVM
import com.telakuR.easyorder.main.ui.theme.Background
import com.telakuR.easyorder.main.ui.theme.CustomTextField
import com.telakuR.easyorder.main.ui.theme.MainButton
import com.telakuR.easyorder.main.ui.theme.Toolbar

@Composable
fun ForgotPasswordScreen(navController: NavHostController, viewModel: LoginVM = hiltViewModel()) {
    val uiState by viewModel.uiState

    Scaffold(
        topBar = {
            TopAppBar(navController = navController)
        },
        bottomBar = {
            BottomBar(navController = navController, viewModel = viewModel)
        },
        content = {
            it

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                CustomTextField(
                    stringResource(id = R.string.email),
                    uiState.email,
                    viewModel::onEmailChange,
                    Icons.Filled.Email,
                    Modifier.fillMaxWidth().padding(15.dp)
                )
            }
        },
        backgroundColor = Background
    )
}

@Composable
private fun TopAppBar(navController: NavController) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Toolbar(navController = navController)

        Text(
            text = stringResource(R.string.forgot_your_password),
            modifier = Modifier.padding(start = 10.dp),
            fontSize = 25.sp
        )
    }
}

@Composable
private fun BottomBar(navController: NavController, viewModel: LoginVM) {
    Column(modifier = Modifier.fillMaxWidth()) {
        MainButton(textId = R.string.next) {
            viewModel.onForgotPasswordClick()
            navController.navigate(AuthenticationRoute.Login.route)
        }
    }
}
