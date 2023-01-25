package com.telakuR.easyorder.setupProfile.ui.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.telakuR.easyorder.R
import com.telakuR.easyorder.setupProfile.models.SetUpProfileRoute
import com.telakuR.easyorder.setupProfile.ui.components.GetImageFromDatabase
import com.telakuR.easyorder.setupProfile.viewModels.SetUpProfileViewModel
import com.telakuR.easyorder.ui.theme.Background
import com.telakuR.easyorder.ui.theme.MainButton
import com.telakuR.easyorder.ui.theme.Toolbar

@Composable
fun PicturePreviewScreen(
    navController: NavController,
    viewModel: SetUpProfileViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
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
                viewModel.getImageFromDatabase()
                GetImageFromDatabase(
                    createProfileImageContent = { imageUrl ->
                        ProfileImageContent(imageUrl)
                    }
                )
            }
        },
        backgroundColor = Background
    )
}

@Composable
fun ProfileImageContent(
    imageUrl: String
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .crossfade(true)
            .build(),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .width(245.dp)
            .height(238.dp)
    )
}
