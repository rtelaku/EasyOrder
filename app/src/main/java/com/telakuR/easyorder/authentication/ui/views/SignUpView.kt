package com.telakuR.easyorder.authentication.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.telakuR.easyorder.R
import com.telakuR.easyorder.ui.theme.AppThemeLogo
import com.telakuR.easyorder.ui.theme.CustomPasswordTextField
import com.telakuR.easyorder.ui.theme.CustomTextField
import com.telakuR.easyorder.ui.theme.MainButton

@Composable
fun SignUpScreen(navController: NavController, role: String?) {
    AppThemeLogo(navController)
    SignUpFields()
    SignUpButton(navController, role)
}

@Composable
fun SignUpFields() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    )
    {
        CustomTextField(stringResource(id = R.string.user_name), Icons.Filled.Person)
        CustomTextField(stringResource(id = R.string.email), Icons.Filled.Email)
        CustomPasswordTextField(stringResource(id = R.string.password), false)
    }
}

@Composable
fun SignUpButton(navController: NavController, role: String?) {
    Column(
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 30.dp)
    )
    {
        MainButton(stringResource(id = R.string.create_account), {})
    }
}