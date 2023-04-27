package com.telakuR.easyorder.setupProfile.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import com.telakuR.easyorder.main.models.Response
import com.telakuR.easyorder.setupProfile.viewModel.SetUpPictureVM

@Composable
fun GetImageFromDatabase(
    viewModel: SetUpPictureVM = hiltViewModel(),
    createProfileImageContent: @Composable (imageUrl: String) -> Unit
) {
    when(val getImageFromDatabaseResponse = viewModel.getImageFromDatabaseResponse) {
        is Response.Loading -> ProgressBar()
        is Response.Success -> getImageFromDatabaseResponse.data?.let { imageUrl ->
            createProfileImageContent(imageUrl)
        }
        is Response.Failure -> LaunchedEffect(Unit) {
            print(getImageFromDatabaseResponse.e)
        }
    }
}

