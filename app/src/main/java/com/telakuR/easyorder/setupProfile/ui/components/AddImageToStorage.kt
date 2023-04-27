package com.telakuR.easyorder.setupProfile.ui.components

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import com.telakuR.easyorder.main.models.Response
import com.telakuR.easyorder.setupProfile.viewModel.SetUpPictureVM

@Composable
fun AddImageToStorage(
    viewModel: SetUpPictureVM = hiltViewModel(),
    addImageToDatabase: (downloadUrl: Uri) -> Unit
) {
    when(val addImageToStorageResponse = viewModel.addImageToStorageResponse) {
        is Response.Loading -> ProgressBar()
        is Response.Success -> addImageToStorageResponse.data?.let { downloadUrl ->
            LaunchedEffect(downloadUrl) {
                addImageToDatabase(downloadUrl)
            }
        }
        is Response.Failure -> LaunchedEffect(Unit) {
            print(addImageToStorageResponse.e)
        }
    }
}