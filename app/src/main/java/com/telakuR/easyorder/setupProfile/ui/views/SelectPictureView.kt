package com.telakuR.easyorder.setupProfile.ui.views

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.telakuR.easyorder.R
import com.telakuR.easyorder.utils.MyFileProvider
import com.telakuR.easyorder.setupProfile.route.SetUpProfileRoute
import com.telakuR.easyorder.setupProfile.ui.components.AddImageToDatabase
import com.telakuR.easyorder.setupProfile.ui.components.AddImageToStorage
import com.telakuR.easyorder.setupProfile.ui.components.GetImageFromDatabase
import com.telakuR.easyorder.setupProfile.viewModel.SetUpPictureVM
import com.telakuR.easyorder.ui.theme.Background
import com.telakuR.easyorder.ui.theme.MainButton
import com.telakuR.easyorder.ui.theme.PictureCardView
import com.telakuR.easyorder.utils.ToastUtils.showToast

@Composable
fun SelectPictureScreen(navController: NavController, viewModel: SetUpPictureVM = hiltViewModel()) {

    var hasImage by remember {
        mutableStateOf(false)
    }
    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val galleryLauncher = rememberLauncherForActivityResult(contract =
    ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageUri = uri
        }
    }

    val cameraLauncher =  rememberLauncherForActivityResult(ActivityResultContracts.TakePicture(),
        onResult = { success ->
            hasImage = success
        }
    )

    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val uri = MyFileProvider.getImageUri(context)
            imageUri = uri
            cameraLauncher.launch(uri)
        }
    }
    val permissionCheckResult = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)

    Scaffold(
        topBar = {
            TopAppBar()
        },
        bottomBar = {
            BottomBar(navController = navController, viewModel = viewModel, imageUri = imageUri)
        },
        content = {
            if (hasImage || imageUri != null) {
                imageUri?.let { uri ->
                    HandleImageUri(uri, viewModel)
                }
            }

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                PictureCardView(
                    textId = R.string.from_gallery,
                    painter = painterResource(id = R.drawable.ic_gallery),
                    onClick = {
                        galleryLauncher.launch("image/*")
                    }
                )

                Spacer(modifier = Modifier.height(30.dp))

                PictureCardView(
                    textId = R.string.take_photo,
                    painter = painterResource(id = R.drawable.ic_camera),
                    onClick = {
                        if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                            val uri = MyFileProvider.getImageUri(context)
                            imageUri = uri
                            cameraLauncher.launch(uri)
                        } else {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }
                )
            }
        },
        backgroundColor = Background
    )
}

@Composable
private fun TopAppBar() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp)
    ) {
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
private fun BottomBar(navController: NavController, viewModel: SetUpPictureVM, imageUri: Uri?) {
    Column(modifier = Modifier.fillMaxWidth()) {
        var dbUri:Uri? = null
        viewModel.getImageFromDatabase()
        GetImageFromDatabase(
            createProfileImageContent = { imageUrl ->
                dbUri = imageUri
            }
        )

        val context = LocalContext.current

        MainButton(textId = R.string.next) {
            if(imageUri != null || dbUri != null) {
                navController.navigate(SetUpProfileRoute.PicturePreview.route)
            } else {
                showToast(context = context, messageId = R.string.please_select_picture, length = Toast.LENGTH_SHORT)
            }
        }
    }
}

@Composable
private fun HandleImageUri(imageUri: Uri, viewModel: SetUpPictureVM) {
    viewModel.addImageToStorage(imageUri)

    AddImageToStorage(
        addImageToDatabase = { downloadUrl ->
            viewModel.addImageToDatabase(downloadUrl)
        }
    )

    AddImageToDatabase(
        showSnackBar = { isImageAddedToDatabase ->
            if (isImageAddedToDatabase) {
                showToast(messageId = R.string.picture_added_successfully, length = Toast.LENGTH_SHORT)
            }
        }
    )
}





