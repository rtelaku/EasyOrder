package com.telakuR.easyorder.home.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.telakuR.easyorder.R
import com.telakuR.easyorder.authentication.models.AuthenticationRoute
import com.telakuR.easyorder.authentication.ui.activities.AuthenticationActivity
import com.telakuR.easyorder.home.viewModel.ProfileVM
import com.telakuR.easyorder.setupProfile.ui.views.HandleImageUri
import com.telakuR.easyorder.setupProfile.viewModel.SetUpPictureVM
import com.telakuR.easyorder.ui.theme.*
import com.telakuR.easyorder.utils.MyFileProvider

@Composable
fun ProfileScreen(viewModel: ProfileVM = hiltViewModel()) {
    viewModel.getProfile()

    val uiState by viewModel.uiState
    val screen = viewModel.screenToSetup.collectAsStateWithLifecycle().value
    val showDialog: MutableState<Boolean> = remember { mutableStateOf(false) }
    val showSetupPictureView: MutableState<Boolean> = remember { mutableStateOf(false) }

    val context = LocalContext.current
    if(screen == AuthenticationRoute.Login.route) {
        context.run {
            val intent = Intent(context, AuthenticationActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    Scaffold(content = { it
        Column(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Top) {
                Text(
                    text = stringResource(id = R.string.my_profile),
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
                Column(modifier = Modifier.clickable {
                    showSetupPictureView.value = true
                }) {
                    ProfileImageContent(imageUrl = profile?.profilePic ?: "", width = 200, height = 200)
                }

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

                ProfilePasswordTextField(
                    stringResource(id = R.string.password),
                    uiState.password,
                    Icons.Filled.Password,
                    viewModel::onPasswordChange
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    ItemButton(
                        textId = R.string.edit_profile,
                        backgroundColor = Color.Green,
                        corners = 10
                    ) {
                        showDialog.value = true
                    }

                    ItemButton(
                        textId = R.string.log_out,
                        backgroundColor = Color.Red,
                        corners = 10
                    ) {
                        viewModel.logOut()
                    }
                }
            }

            ConfirmDialog(showDialog = showDialog, viewModel = viewModel)
            SelectImageDialog(showDialog = showSetupPictureView)
        }
    },
        backgroundColor = Background
    )
}

@Composable
private fun ConfirmDialog(showDialog: MutableState<Boolean>, viewModel: ProfileVM) {
    var currentPassword by remember { mutableStateOf("") }

    if (showDialog.value) {
        Dialog(onDismissRequest = { showDialog.value = false }) {
            WhiteItemCard(modifier = Modifier.padding(16.dp)) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ProfilePasswordTextField(
                        stringResource(id = R.string.password),
                        currentPassword,
                        Icons.Filled.Password,
                    ) { currentPassword = it }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        colors = ButtonDefaults.buttonColors(backgroundColor = PrimaryColor),
                        shape = RoundedCornerShape(15.dp),
                        onClick = {
                            viewModel.editProfile(currentPassword = currentPassword)
                            showDialog.value = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(stringResource(id = R.string.confirm), color = Color.White)
                    }
                }
            }
        }
    }
}

//TODO("Make this reusable")
@Composable
private fun SelectImageDialog(showDialog: MutableState<Boolean>, setUpPictureVM: SetUpPictureVM = hiltViewModel()) {
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

    val context = LocalContext.current
    val cameraLauncher =  rememberLauncherForActivityResult(ActivityResultContracts.TakePicture(),
        onResult = { success ->
            hasImage = success
        }
    )

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

    if (hasImage || imageUri != null) {
        imageUri?.let { uri ->
            HandleImageUri(uri, setUpPictureVM)
            showDialog.value = false
        }
    }

    if (showDialog.value) {

        Dialog(onDismissRequest = { showDialog.value = false }) {
            WhiteItemCard(modifier = Modifier.padding(16.dp)) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
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
            }
        }
    }
}
