package com.telakuR.easyorder.authentication.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.telakuR.easyorder.R
import com.telakuR.easyorder.authentication.viewmodel.LoginVM
import com.telakuR.easyorder.ui.theme.*

@Composable
fun LoginScreen(navController: NavController, viewModel: LoginVM = hiltViewModel()) {
    val uiState by viewModel.uiState

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(Background)) {
        AppThemeLogo()

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        )
        {
            CustomTextField(
                stringResource(id = R.string.email),
                uiState.email,
                viewModel::onEmailChange,
                Icons.Filled.Email
            )
            CustomPasswordTextField(stringResource(id = R.string.password), uiState.password, false, viewModel::onPasswordChange)
            TextButton(onClick = { navController.navigate(Route.ForgotPassword.route) }) {
                Text(
                    text = stringResource(id = R.string.forgot_your_password),
                    color = PrimaryColor,
                    fontSize = 14.sp
                )
            }
        }

        Column(
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
        )
        {
            MainButton(stringResource(id = R.string.login)) {
                viewModel::onSignInClick.invoke()
            }
            TextButton(onClick = { navController.navigate(Route.ChooseRole.route) }) {
                Text(
                    text = stringResource(id = R.string.do_not_have_an_account),
                    color = PrimaryColor,
                    fontSize = 14.sp
                )
            }
        }
    }
}

