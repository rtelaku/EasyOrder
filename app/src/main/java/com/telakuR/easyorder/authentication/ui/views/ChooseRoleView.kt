package com.telakuR.easyorder.authentication.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.telakuR.easyorder.R
import com.telakuR.easyorder.authentication.enums.GeneralRoleEnum
import com.telakuR.easyorder.ui.theme.Background
import com.telakuR.easyorder.ui.theme.CardView
import com.telakuR.easyorder.ui.theme.Route
import com.telakuR.easyorder.ui.theme.Toolbar

@Composable
fun ChooseRole(navController: NavController) {
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
            Text(
                text = stringResource(R.string.continue_as),
                modifier = Modifier.padding(start = 10.dp),
                fontSize = 25.sp
            )
        }

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            val user = GeneralRoleEnum.USER.role
            val business = GeneralRoleEnum.BUSINESS.role
            CardView(
                text = user,
                onClick = { navController.navigate(Route.SignUp.route + "/$user") })
            Spacer(modifier = Modifier.height(20.dp))
            CardView(
                text = business,
                onClick = { navController.navigate(Route.SignUp.route + "/$business") })
        }
    }
}