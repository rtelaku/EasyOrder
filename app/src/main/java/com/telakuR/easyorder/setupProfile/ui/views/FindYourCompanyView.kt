package com.telakuR.easyorder.setupProfile.ui.views

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.telakuR.easyorder.R
import com.telakuR.easyorder.setupProfile.models.SetUpProfileRoute
import com.telakuR.easyorder.setupProfile.viewModels.SetUpProfileViewModel
import com.telakuR.easyorder.ui.theme.Background
import com.telakuR.easyorder.ui.theme.MainButton
import com.telakuR.easyorder.ui.theme.SearchBar
import com.telakuR.easyorder.ui.theme.Toolbar

@ExperimentalAnimationApi
@Composable
fun FindYourCompanyScreen(navController: NavController, viewModel: SetUpProfileViewModel = hiltViewModel()) {
    Scaffold(
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Toolbar(navController = navController)

                Text(
                    text = stringResource(R.string.find_your_company),
                    modifier = Modifier.padding(start = 10.dp),
                    fontSize = 25.sp
                )

                val items = listOf(
                    "Paulo Pereira",
                    "Daenerys Targaryen",
                    "Jon Snow",
                    "Sansa Stark",
                )
                SearchBar(items = items)
            }
        },
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                MainButton(text = stringResource(id = R.string.next)) {
                    navController.navigate(SetUpProfileRoute.FindYourCompany.route)
                }
            }
        },
        content = {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {

            }
        },
        backgroundColor = Background
    )
}
