package com.telakuR.easyorder.setupProfile.ui.views

import android.content.Intent
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.telakuR.easyorder.R
import com.telakuR.easyorder.home.ui.activities.HomeActivity
import com.telakuR.easyorder.models.User
import com.telakuR.easyorder.setupProfile.viewModel.FindCompanyVM
import com.telakuR.easyorder.ui.theme.*
import com.telakuR.easyorder.utils.ToastUtils.showToast
import java.util.*

@ExperimentalAnimationApi
@Composable
fun FindYourCompanyScreen(
    navController: NavController,
    viewModel: FindCompanyVM = hiltViewModel(),
    shownFromUser: Boolean
) {
    viewModel.getCompanies()
    val textState = remember { mutableStateOf(TextFieldValue("")) }
    val selected: MutableState<Boolean> = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                navController = navController,
                shownFromUser = shownFromUser,
                viewModel = viewModel,
                textState = textState
            )
        },
        bottomBar = {
            BottomBar(selected = selected)
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
                val companies = viewModel.companies.collectAsState().value
                CompaniesList(companies = companies, state = textState, selected = selected, viewModel = viewModel)
            }
        },
        backgroundColor = Background
    )
}

@Composable
private fun CompaniesList(
    companies: List<User>,
    selected: MutableState<Boolean>,
    state: MutableState<TextFieldValue>,
    viewModel: FindCompanyVM
) {
    viewModel.getSelectedCompany()

    val selectedCompany: MutableState<String> = remember { mutableStateOf("") }

    val previousCompany = viewModel.previousRequestedCompany.collectAsState().value

    if(!previousCompany.isNullOrEmpty()) {
        selected.value = true
        selectedCompany.value = previousCompany
    }

    var filteredCompanies: List<User>
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        val searchedText = state.value.text
        filteredCompanies = if (searchedText.isEmpty()) {
            companies
        } else {
            val resultList = ArrayList<User>()
            for (company in companies) {
                if (company.name.lowercase(Locale.getDefault())
                        .contains(searchedText.lowercase(Locale.getDefault()))
                ) {
                    resultList.add(company)
                }
            }
            resultList
        }
        items(filteredCompanies) { filteredCompany ->
            CompanyListItem(company = filteredCompany, selected = selected, selectedCompany = selectedCompany, viewModel = viewModel)
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun TopAppBar(navController: NavController, shownFromUser: Boolean, viewModel: FindCompanyVM, textState:  MutableState<TextFieldValue>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Toolbar(navController = navController, shownFromUser = shownFromUser)

        Text(
            text = stringResource(R.string.find_your_company),
            modifier = Modifier
                .padding(start = 10.dp),
            fontSize = 25.sp
        )

        val companiesByName = viewModel.companies.collectAsState().value.map { it.name }
        SearchBar(searchTextId =  R.string.where_do_you_work, items = companiesByName, textState = textState)
    }
}

@Composable
private fun BottomBar(selected: MutableState<Boolean>) {
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxWidth()) {
        MainButton(textId = R.string.continue_text) {
            if(selected.value) {
                context.run {
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            } else {
                showToast(context = context, messageId = R.string.please_select_a_company, length = Toast.LENGTH_SHORT)
            }
        }
    }
}

@Composable
private fun CompanyListItem(company: User, selectedCompany: MutableState<String>, selected: MutableState<Boolean>, viewModel: FindCompanyVM) {
    val color = if (selectedCompany.value == company.email) Background else PrimaryColor
    val textId = if (selectedCompany.value == company.email) R.string.requested else R.string.request_to_join

    WhiteItemCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Column {
                    AsyncRoundedImage(
                        image = company.profilePic,
                        size = 65,
                        cornerSize = 10
                    )
                }

                Column(
                    modifier = Modifier
                        .padding(start = 10.dp)
                ) {
                    Text(text = company.name)
                }
            }

            Row(horizontalArrangement = Arrangement.End) {
                ItemButton(
                    textId = textId,
                    backgroundColor = color
                ) {
                    selected.value = !selected.value
                    selectedCompany.value = if (selectedCompany.value == company.email) "" else company.email
                    viewModel.handleRequestState(id = company.id, state = selected.value)
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(10.dp))
}


