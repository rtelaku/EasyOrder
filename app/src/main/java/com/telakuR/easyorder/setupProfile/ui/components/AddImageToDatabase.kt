package com.telakuR.easyorder.setupProfile.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import com.telakuR.easyorder.Response
import com.telakuR.easyorder.setupProfile.viewModels.SetUpProfileViewModel

@Composable
fun AddImageToDatabase(
    viewModel: SetUpProfileViewModel = hiltViewModel(),
    showSnackBar: (isImageAddedToDatabase: Boolean) -> Unit
) {
    when(val addImageToDatabaseResponse = viewModel.addImageToDatabaseResponse) {
        is Response.Loading -> ProgressBar()
        is Response.Success -> addImageToDatabaseResponse.data?.let { isImageAddedToDatabase ->
            LaunchedEffect(isImageAddedToDatabase) {
                showSnackBar(isImageAddedToDatabase)
            }
        }
        is Response.Failure -> LaunchedEffect(Unit) {
            print(addImageToDatabaseResponse.e)
        }
    }
}