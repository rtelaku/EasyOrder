package com.telakuR.easyorder.setupProfile.ui.views

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.telakuR.easyorder.R
import com.telakuR.easyorder.components.autocomplete.AutoCompleteBox
import com.telakuR.easyorder.components.autocomplete.utils.AutoCompleteSearchBarTag
import com.telakuR.easyorder.components.autocomplete.utils.asAutoCompleteEntities
import com.telakuR.easyorder.setupProfile.models.SetUpProfileRoute
import com.telakuR.easyorder.setupProfile.viewModels.SetUpProfileViewModel
import com.telakuR.easyorder.ui.theme.Background
import com.telakuR.easyorder.ui.theme.MainButton
import com.telakuR.easyorder.ui.theme.SearchBar
import com.telakuR.easyorder.ui.theme.Toolbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

@ExperimentalAnimationApi
@Composable
fun FindYourCompanyScreen(navController: NavController, viewModel: SetUpProfileViewModel = hiltViewModel()) {
    viewModel.getCompanies()

    Scaffold(
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Toolbar(navController = navController)

                Text(
                    text = stringResource(R.string.find_your_company),
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .width(150.dp),
                    fontSize = 25.sp
                )

                val companies = viewModel.companies.collectAsState().value.map { it.name }

                SearchBar(items = companies)

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


