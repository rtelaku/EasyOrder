package com.telakuR.easyorder.authentication.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.telakuR.easyorder.R
import com.telakuR.easyorder.ui.theme.*

@Composable
fun LoginScreen(navController: NavController) {
    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
        color = Background
    ) {
        AppThemeLogo()
        LoginFields()
        LoginButton()
    }
}

@Composable
fun LoginFields() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    )
    {
        CustomTextField(stringResource(id = R.string.email), Icons.Filled.Email)
        CustomPasswordTextField(stringResource(id = R.string.password), false)
        Text(
            text = stringResource(id = R.string.forgot_your_password),
            color = PrimaryColor,
            fontSize = 14.sp,
            modifier = Modifier.padding(5.dp)
        )
    }
}

@Composable
fun LoginButton() {
    Column(
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 30.dp)
    )
    {
        MainButton(stringResource(id = R.string.login))
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = stringResource(id = R.string.do_not_have_an_account),
            color = PrimaryColor,
            fontSize = 14.sp
        )
    }
}