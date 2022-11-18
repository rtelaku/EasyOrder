package com.telakuR.easyorder.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.telakuR.easyorder.R

private val DarkColorPalette = darkColors(
    primary = Color.White,
    background = DarkGray,
    onBackground = Color.White,
    surface = Color.Black,
    onSurface = DarkGray
)

private val LightColorPalette = lightColors(
    primary = Color.White,
    secondary = Color.Black
)

@Composable
fun EasyOrderTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

@Composable
fun AppThemeLogo() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.ic_easy_order_logo),
            contentDescription = "EasyOrder logo",
        )

        Text(text = stringResource(R.string.app_name),
            fontSize = 40.sp,
            color = PrimaryColor,
            fontWeight = FontWeight.Bold)

        Text(text = stringResource(R.string.easy_order_description),
            fontSize = 15.sp
        )
    }
}

@Composable
fun Toolbar(navController: NavController) {
    TopAppBar(
        elevation = 0.dp,
        title = {},
        navigationIcon = {
            IconButton(onClick = {  navController.popBackStack() }) {
                Image(
                    painter = painterResource(R.drawable.ic_easy_order_logo),
                    contentDescription = "Back arrow"
                )
            }
        },
        backgroundColor = Color.Transparent,
    )
}

@Composable
fun CustomTextField(labelValue: String, imageVector: ImageVector) {
    var textState by remember { mutableStateOf("") }
    TextField(
        leadingIcon = {
            Icon(imageVector = imageVector, null, tint = PrimaryColor)
        },
        value = textState,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.White,
            cursorColor = Color.Black,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        onValueChange = {
            textState = it
        },
        modifier = Modifier
            .padding(10.dp)
            .width(280.dp),
        placeholder = { Text(labelValue) },
        shape = RoundedCornerShape(8.dp),
        singleLine = true,
        trailingIcon = {
            if (textState.isNotEmpty()) {
                IconButton(onClick = { textState = "" }) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = null
                    )
                }
            }
        }
    )
}

@Composable
fun CustomPasswordTextField(labelValue: String, showPassword: Boolean) {
    var textState by remember { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(showPassword) }

    TextField(
        value = textState,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.White,
            cursorColor = Color.Black,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        onValueChange = {
            textState = it
        },
        leadingIcon = {
            Icon(imageVector = Icons.Default.Lock, null, tint = PrimaryColor)
        },
        visualTransformation = if (passwordVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        trailingIcon = {
            val image = if (passwordVisible)
                Icons.Filled.Visibility
            else Icons.Filled.VisibilityOff

            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(imageVector = image, null)
            }
        },
        modifier = Modifier
            .padding(10.dp)
            .width(280.dp),
        placeholder = { Text(labelValue) },
        shape = RoundedCornerShape(8.dp),
        singleLine = true
    )
}

@Composable
fun MainButton(text: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        OutlinedButton(
            onClick = { onClick.invoke() },
            modifier = Modifier
                .widthIn(min = 150.dp)
                .height(70.dp)
                .padding(5.dp),
            shape = RoundedCornerShape(20.dp),
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                backgroundColor = PrimaryColor,
                contentColor = Color.White
            )
        ) {
            Text(text = text)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CardView(text: String, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(8.dp),
        backgroundColor = MaterialTheme.colors.surface,
        onClick = { onClick.invoke() }) {
        Column(
            modifier = Modifier
                .width(280.dp)
                .height(130.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(text = text, fontSize = 14.sp)
        }
    }
}