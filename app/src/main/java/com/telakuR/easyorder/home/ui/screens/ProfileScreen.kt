package com.telakuR.easyorder.home.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.telakuR.easyorder.R
import com.telakuR.easyorder.setupProfile.ui.views.ProfileImageContent
import com.telakuR.easyorder.ui.theme.Background
import com.telakuR.easyorder.ui.theme.CustomPasswordTextFieldd
import com.telakuR.easyorder.ui.theme.CustomTextField
import com.telakuR.easyorder.ui.theme.ItemButton
import com.telakuR.easyorder.viewModels.HomeVM

@Composable
fun ProfileScreen(viewModel: HomeVM = hiltViewModel()) {
    viewModel.getProfile()

    val uiState by viewModel.uiState

    Scaffold(content = { it
        Column(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Top) {
                Text(
                    text = "My Profile",
                    modifier = Modifier.padding(start = 10.dp),
                    fontSize = 25.sp
                )
            }

            val profile = viewModel.profile.collectAsState().value

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProfileImageContent(imageUrl = profile.profilePic, width = 200, height = 200)

                Spacer(modifier = Modifier.height(20.dp))

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

                CustomPasswordTextFieldd(
                    stringResource(id = R.string.password),
                    uiState.password,
                    Icons.Filled.Password,
                    viewModel::onPasswordChange
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    ItemButton(
                        text = "Edit Profile",
                        backgroundColor = Color.Green,
                        corners = 10
                    ) {
                        viewModel.editProfile()
                    }

                    ItemButton(
                        text = "Log Out",
                        backgroundColor = Color.Red,
                        corners = 10
                    ) {
                        viewModel.logOut()
                    }
                }
            }

        }
    },
        backgroundColor = Background
    )
}