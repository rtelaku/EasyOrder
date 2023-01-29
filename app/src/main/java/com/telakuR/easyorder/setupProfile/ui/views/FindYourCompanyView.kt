package com.telakuR.easyorder.setupProfile.ui.views

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
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
import com.telakuR.easyorder.models.User
import com.telakuR.easyorder.setupProfile.viewModels.SetUpProfileViewModel
import com.telakuR.easyorder.ui.activities.HomeActivity
import com.telakuR.easyorder.ui.theme.*
import com.telakuR.easyorder.utils.ToastUtils.showToast
import java.util.*

@ExperimentalAnimationApi
@Composable
fun FindYourCompanyScreen(
    navController: NavController,
    viewModel: SetUpProfileViewModel = hiltViewModel()
) {
    viewModel.getCompanies()
    val textState = remember { mutableStateOf(TextFieldValue("")) }
    val selected: MutableState<Boolean> = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Toolbar(navController = navController)

                Text(
                    text = stringResource(R.string.find_your_company),
                    modifier = Modifier
                        .padding(start = 10.dp),
                    fontSize = 25.sp
                )

                val companiesByName = viewModel.companies.collectAsState().value.map { it.name }
                SearchBar(items = companiesByName, textState = textState)
            }
        },
        bottomBar = {
            val context = LocalContext.current
            Column(modifier = Modifier.fillMaxWidth()) {
                MainButton(text = stringResource(id = R.string.continue_text)) {
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
fun CompaniesList(
    companies: List<User>,
    selected: MutableState<Boolean>,
    state: MutableState<TextFieldValue>,
    viewModel: SetUpProfileViewModel
) {
    val selectedCompany: MutableState<String> = remember { mutableStateOf("") }

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

@Composable
fun CompanyListItem(company: User, selectedCompany: MutableState<String>, selected: MutableState<Boolean>, viewModel: SetUpProfileViewModel) {
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
                    text = stringResource(id = textId),
                    backgroundColor = color
                ) {
                    selected.value = !selected.value
                    selectedCompany.value = if (selectedCompany.value == company.email) "" else company.email
                    viewModel.handleRequestState(email = company.email, state = selected.value)
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(10.dp))
}


