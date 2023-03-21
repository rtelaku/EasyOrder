package com.telakuR.easyorder.authentication.ui.views

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.telakuR.easyorder.R
import com.telakuR.easyorder.authentication.models.AuthenticationRoute
import com.telakuR.easyorder.authentication.viewmodel.LoginVM
import com.telakuR.easyorder.home.route.HomeRoute
import com.telakuR.easyorder.home.ui.HomeActivity
import com.telakuR.easyorder.setupProfile.route.SetUpProfileRoute
import com.telakuR.easyorder.setupProfile.ui.activities.SetUpProfileActivity
import com.telakuR.easyorder.ui.theme.*
import com.telakuR.easyorder.utils.ToastUtils.showToast

@Composable
fun LoginScreen(navController: NavController, viewModel: LoginVM = hiltViewModel()) {
    val uiState by viewModel.uiState

    Scaffold(
        topBar = {
            AppThemeLogo()
        },
        bottomBar = {
            BottomAppBar(
                navController = navController,
                viewModel = viewModel
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(it),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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

                TextButton(onClick = { navController.navigate(AuthenticationRoute.ForgotPassword.route) }) {
                    Text(
                        text = stringResource(id = R.string.forgot_your_password),
                        color = PrimaryColor,
                        fontSize = 14.sp
                    )
                }
            }

            val screenToSetup = viewModel.screenToSetup.collectAsState().value

            val context = LocalContext.current

            if(screenToSetup == HomeRoute.Home.route) {
                startHomeActivity(context)
            } else if(screenToSetup == SetUpProfileRoute.SelectPicture.route) {
                startSetupProfileActivity(context)
            }

            val toastMessageId = viewModel.toastMessageId.collectAsState().value
            if(toastMessageId != null) showToast(context = context, messageId = toastMessageId, length = Toast.LENGTH_SHORT)

        },
        backgroundColor = Background
    )
}

private fun startHomeActivity(context: Context) {
    context.run {
        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}

private fun startSetupProfileActivity(context: Context) {
    context.run {
        val intent = Intent(this, SetUpProfileActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}

@Composable
private fun BottomAppBar(navController: NavController, viewModel: LoginVM) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MainButton(textId = R.string.login) {
            viewModel::onSignInClick.invoke()
        }
        TextButton(onClick = { navController.navigate(AuthenticationRoute.SignUp.route) }) {
            Text(
                text = stringResource(id = R.string.do_not_have_an_account),
                color = PrimaryColor,
                fontSize = 14.sp
            )
        }
    }
}
