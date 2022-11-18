package com.telakuR.easyorder.authentication.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.telakuR.easyorder.R
import com.telakuR.easyorder.ui.theme.*

@Composable
fun LoginScreen(navController: NavController) {
    AppThemeLogo(navController)
    LoginFields(navController)
    LoginButton(navController)
}

@Composable
fun LoginFields(navController: NavController) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    )
    {
        CustomTextField(stringResource(id = R.string.email), Icons.Filled.Email)
        CustomPasswordTextField(stringResource(id = R.string.password), false)
        TextButton(onClick = { navController.navigate(Route.ForgotPassword.route) }) {
            Text(
                text = stringResource(id = R.string.forgot_your_password),
                color = PrimaryColor,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun LoginButton(navController: NavController) {
    Column(
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth())
    {
        MainButton(stringResource(id = R.string.login)) { }
        TextButton(onClick = { navController.navigate(Route.ChooseRole.route) }) {
            Text(
                text = stringResource(id = R.string.do_not_have_an_account),
                color = PrimaryColor,
                fontSize = 14.sp
            )
        }
    }
}