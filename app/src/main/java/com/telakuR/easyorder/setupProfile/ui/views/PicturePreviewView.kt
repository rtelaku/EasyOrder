package com.telakuR.easyorder.setupProfile.ui.views

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.telakuR.easyorder.R
import com.telakuR.easyorder.home.ui.activities.HomeActivity
import com.telakuR.easyorder.main.enums.RolesEnum
import com.telakuR.easyorder.main.ui.theme.Background
import com.telakuR.easyorder.main.ui.theme.MainButton
import com.telakuR.easyorder.main.ui.theme.ProfileImageContent
import com.telakuR.easyorder.main.ui.theme.Toolbar
import com.telakuR.easyorder.setupProfile.route.SetUpProfileRoute
import com.telakuR.easyorder.setupProfile.ui.components.GetImageFromDatabase
import com.telakuR.easyorder.setupProfile.viewModel.SetUpPictureVM

@Composable
fun PicturePreviewScreen(
    navController: NavController,
    viewModel: SetUpPictureVM = hiltViewModel()
) {
    viewModel.getImageFromDatabase()

    Scaffold(
        topBar = {
            TopAppBar(navController = navController)
        },
        bottomBar = {
            BottomBar(navController = navController, viewModel = viewModel)
        },
        content = {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                GetImageFromDatabase(
                    createProfileImageContent = { imageUrl ->
                        ProfileImageContent(imageUrl = imageUrl, width = 245, height = 238)
                    }
                )
            }
        },
        backgroundColor = Background
    )
}

@Composable
private fun TopAppBar(navController: NavController) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Toolbar(navController = navController)

        Text(
            text = stringResource(R.string.upload_photo),
            modifier = Modifier.padding(start = 10.dp),
            fontSize = 25.sp
        )

        Text(
            text = stringResource(R.string.display_data_for_security),
            modifier = Modifier.padding(start = 10.dp),
            fontSize = 12.sp
        )
    }
}

@Composable
private fun BottomBar(navController: NavController, viewModel: SetUpPictureVM) {
    val role = viewModel.currentUserRole.collectAsStateWithLifecycle().value
    val context = LocalContext.current


    Column(modifier = Modifier.fillMaxWidth()) {
        MainButton(textId = R.string.next) {
            if(role == RolesEnum.USER.name) {
                navController.navigate(SetUpProfileRoute.FindYourCompany.route)
            } else if(role == RolesEnum.COMPANY.name) {
                context.run {
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            }
        }
    }
}
