package com.telakuR.easyorder.authentication.ui.views

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.telakuR.easyorder.R
import com.telakuR.easyorder.authentication.models.AuthenticationRoute
import com.telakuR.easyorder.authentication.viewmodel.SignUpVM
import com.telakuR.easyorder.ui.theme.*
import com.telakuR.easyorder.utils.ToastUtils

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    navController: NavController,
    viewModel: SignUpVM = hiltViewModel()
) {
    val uiState by viewModel.uiState
    var role = viewModel.getRoles()[0].role

    Scaffold(
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Toolbar(navController = navController)
                AppThemeLogo()
            }
        },
        bottomBar = {
            MainButton(stringResource(id = R.string.create_account)) {
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
                                    role = selectionOption.role
                                    expanded = false
                                },
                                modifier = Modifier.background(Color.White)
                            ) {
                                Text(text = selectionOption.role)
                            }
                        }
                    }
                }
            }
        },
        backgroundColor = Background
    )

    val message = viewModel.toastMessage.collectAsState().value
    val shouldDisplayToast = message.isNotEmpty()
    if(shouldDisplayToast) ToastUtils.showToast(context = LocalContext.current, message = message, length = Toast.LENGTH_SHORT)

    val shouldShowLoginView = viewModel.shouldShowLoginView.collectAsState().value
    if(shouldShowLoginView) navController.navigate(AuthenticationRoute.Login.route)

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandedTextField(
    textState: String,
    imageVector: ImageVector,
    modifier: Modifier,
    expanded: Boolean,
    onValueChanged: (String) -> Unit
) {
    var text = textState
    TextField(
        leadingIcon = {
            Icon(imageVector = imageVector, null, tint = PrimaryColor)
        },
        value = text,
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color.Black,
            cursorColor = Color.Black,
            containerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        enabled = false,
        onValueChange = {
            text = it
            onValueChanged(it)
        },
        trailingIcon = {
            ExposedDropdownMenuDefaults.TrailingIcon(
                expanded = expanded
            )
        },
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        singleLine = true
    )
}