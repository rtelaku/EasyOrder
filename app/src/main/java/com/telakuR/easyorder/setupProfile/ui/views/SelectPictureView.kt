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
import com.telakuR.easyorder.setupProfile.models.MyFileProvider
import com.telakuR.easyorder.setupProfile.models.SetUpProfileRoute
import com.telakuR.easyorder.setupProfile.ui.components.AddImageToDatabase
import com.telakuR.easyorder.setupProfile.ui.components.AddImageToStorage
import com.telakuR.easyorder.setupProfile.ui.components.GetImageFromDatabase
import com.telakuR.easyorder.setupProfile.viewModels.SetUpProfileViewModel
import com.telakuR.easyorder.ui.theme.Background
import com.telakuR.easyorder.ui.theme.MainButton
import com.telakuR.easyorder.ui.theme.PictureCardView
import com.telakuR.easyorder.utils.ToastUtils.showToast

@Composable
fun SelectPictureScreen(navController: NavController, viewModel: SetUpProfileViewModel = hiltViewModel()) {

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
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 30.dp)) {
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
                //ADD THIS DATA ON ROOM DB
               var uri:Uri? = null
                viewModel.getImageFromDatabase()
                GetImageFromDatabase(
                    createProfileImageContent = { imageUrl ->
                       uri = imageUri
                    }
                )
                MainButton(text = stringResource(id = R.string.next)) {
                   if(uri != null) {
                       navController.navigate(SetUpProfileRoute.PicturePreview.route)
                   } else {
                       showToast(context = context, message = "Please select a picture for your profile", length = Toast.LENGTH_SHORT)
                   }
                }
            }
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
                    painter = painterResource(id = R.drawable.ic_gallery_icon),
                    onClick = {
                        galleryLauncher.launch("image/*")
                    }
                )

                Spacer(modifier = Modifier.height(30.dp))

                PictureCardView(
                    painter = painterResource(id = R.drawable.ic_camera_icon),
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
fun HandleImageUri(imageUri: Uri, viewModel: SetUpProfileViewModel) {
    viewModel.addImageToStorage(imageUri)

    AddImageToStorage(
        addImageToDatabase = { downloadUrl ->
            showToast(message = "Picture added successfully", length = Toast.LENGTH_SHORT)
            viewModel.addImageToDatabase(downloadUrl)
        }
    )

    AddImageToDatabase(
        showSnackBar = { isImageAddedToDatabase ->
            if (isImageAddedToDatabase) {
                showToast(message = "Picture added successfully", length = Toast.LENGTH_SHORT)
            }
        }
    )
}





