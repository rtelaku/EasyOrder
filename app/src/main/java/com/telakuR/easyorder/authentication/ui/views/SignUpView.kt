package com.telakuR.easyorder.authentication.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.telakuR.easyorder.R
import com.telakuR.easyorder.authentication.viewmodel.SignUpVM
import com.telakuR.easyorder.ui.theme.*

@Composable
fun SignUpScreen(navController: NavController, role: String?, viewModel: SignUpVM = hiltViewModel()) {
    val uiState by viewModel.uiState

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(Background)) {
        Column(
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxWidth()
        ) {
            Toolbar(navController = navController)
            AppThemeLogo()
        }

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        )
        {
//            CustomTextField(
//                stringResource(id = R.string.user_name),
//                viewModel::onEmailChange,
//                Icons.Filled.Person
//            )
            CustomTextField(
                stringResource(id = R.string.email),
                uiState.email,
                viewModel::onEmailChange,
                Icons.Filled.Email
            )
            CustomPasswordTextField(
                stringResource(id = R.string.password),
                uiState.password,
                false,
                viewModel::onPasswordChange
            )
        }

        Column(
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 30.dp)
        )
        {
            MainButton(stringResource(id = R.string.create_account)) {
                if (role != null) {
                    viewModel.onSignUpClick(role)
                }
            }
        }
    }
}