package com.telakuR.easyorder.authentication.ui.views

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.telakuR.easyorder.R
import com.telakuR.easyorder.authentication.models.AuthenticationRoute
import com.telakuR.easyorder.authentication.viewmodel.SignUpVM
import com.telakuR.easyorder.main.ui.theme.*
import com.telakuR.easyorder.utils.ToastUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    navController: NavController,
    viewModel: SignUpVM = hiltViewModel()
) {
    val uiState by viewModel.uiState
    var role = viewModel.getRoles()[0].name

    Scaffold(
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Toolbar(navController = navController)
                AppThemeLogo()
            }
        },
        bottomBar = {
            MainButton(textId = R.string.create_account) {
                viewModel.onSignUpClick(role)
            }
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CustomTextField(
                    stringResource(id = R.string.user_name),
                    uiState.name,
                    viewModel::onNameChanged,
                    Icons.Filled.Person
                )

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

                var expanded by remember {
                    mutableStateOf(false)
                }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = {
                        expanded = !expanded
                    }
                ) {
                    ExpandedTextField(
                        textState = role,
                        imageVector = Icons.Filled.Person,
                        modifier = Modifier
                            .padding(10.dp)
                            .width(280.dp)
                            .menuAnchor(),
                        expanded = expanded,
                        viewModel::onRoleChanged
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        viewModel.getRoles().forEach { selectionOption ->
                            DropdownMenuItem(
                                onClick = {
                                    role = selectionOption.name
                                    expanded = false
                                },
                                modifier = Modifier.background(Color.White)
                            ) {
                                val role = stringResource(id = selectionOption.role)
                                Text(text = role)
                            }
                        }
                    }
                }
            }

            val messageId = viewModel.toastMessageId.collectAsState().value
            if(messageId != null) ToastUtils.showToast(context = LocalContext.current, messageId = messageId, length = Toast.LENGTH_SHORT)

            val shouldShowLoginView = viewModel.shouldShowLoginView.collectAsState().value
            if(shouldShowLoginView) navController.popBackStack(AuthenticationRoute.Login.route, false)

        },
        backgroundColor = Background
    )
}