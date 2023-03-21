package com.telakuR.easyorder.ui.theme

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.telakuR.easyorder.R
import com.telakuR.easyorder.home.route.HomeRoute

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
fun Toolbar(navController: NavController, shownFromUser: Boolean = false) {
    TopAppBar(
        elevation = 0.dp,
        title = {},
        navigationIcon = {
            if(!shownFromUser) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Image(
                        painter = painterResource(R.drawable.ic_back_arrow),
                        contentDescription = "Back arrow"
                    )
                }
            }
        },
        backgroundColor = Color.Transparent,
    )
}

@Composable
fun CustomTextField(labelValue: String, textState: String, onNewValue: (String) -> Unit, imageVector: ImageVector) {
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
            onNewValue(it)
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
fun CustomPasswordTextField(labelValue: String, textState: String, showPassword: Boolean, onValueChanged: (String) -> Unit) {
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
            onValueChanged(it)
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
fun ProfilePasswordTextField(labelValue: String, textState: String, imageVector: ImageVector, onValueChanged: (String) -> Unit) {
    TextField(
        value = textState,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.White,
            cursorColor = Color.Black,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        onValueChange = {
            onValueChanged(it)
        },
        leadingIcon = {
            Icon(imageVector = imageVector, null, tint = PrimaryColor)
        },
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier
            .padding(10.dp)
            .width(280.dp),
        placeholder = { Text(labelValue) },
        shape = RoundedCornerShape(8.dp),
        singleLine = true
    )
}

@Composable
fun MainButton(textId: Int, enabled: Boolean = true, onClick: () -> Unit) {
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
            enabled = enabled,
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                backgroundColor = PrimaryColor,
                contentColor = Color.White
            )
        ) {
            Text(text = stringResource(id = textId))
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PictureCardView(textId: Int, painter: Painter, onClick: () -> Unit) {
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
        ) {
            Image(
                painter = painter,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(48.dp),
                contentDescription = "Picture holder"
            )

            Text(text = stringResource(id = textId), fontSize = 14.sp, textAlign = TextAlign.Center)
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun SearchBar(searchTextId: Int, items: List<String>, textState: MutableState<TextFieldValue>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 15.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (items.isNotEmpty()) {
            val view = LocalView.current
            val focusRequester = remember { FocusRequester() }

            TextSearchBar(
                searchText = stringResource(id = searchTextId),
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .focusable(),
                onDoneActionClick = {
                    view.clearFocus()
                },
                state = textState,
                onClearClick = {
                    view.clearFocus()
                },
                onFocusChanged = { focusState ->
                    if (!focusState.isFocused) {
                        focusRequester.requestFocus()
                    } else {
                        focusRequester.freeFocus()
                    }
                }
            )
        }
    }
}

@Composable
fun TextSearchBar(
    searchText: String,
    modifier: Modifier = Modifier,
    state: MutableState<TextFieldValue>,
    onDoneActionClick: () -> Unit = {},
    onClearClick: () -> Unit = {},
    onFocusChanged: (FocusState) -> Unit = {},
) {
    TextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .onFocusChanged {
                onFocusChanged(it)
            },
        value = state.value,
        onValueChange = { value ->
            state.value = value
        },
        label = { Text(text = searchText) },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = LightOrangeColor,
            unfocusedLabelColor = OrangeTextColor,
            focusedLabelColor = OrangeTextColor
        ),
        shape = RoundedCornerShape(CornerSize(15.dp)),
        singleLine = true,
        leadingIcon = {
            Image(
                painter = painterResource(id = R.drawable.ic_icon_search),
                contentDescription = "Search icon"
            )
        },
        trailingIcon = {
            IconButton(onClick = {
                onClearClick()
                state.value = TextFieldValue("")
            }) {
                Icon(imageVector = Icons.Filled.Clear, contentDescription = "Clear")
            }
        },
        keyboardActions = KeyboardActions(onDone = { onDoneActionClick() }),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Text
        )
    )
}

@Composable
fun WhiteItemCard(modifier: Modifier, content: @Composable () -> Unit) {
    Card(
        modifier = modifier,
        elevation = 1.dp,
        backgroundColor = Color.White,
        shape = RoundedCornerShape(corner = CornerSize(20.dp))
    ) {
        content.invoke()
    }
}

@Composable
fun AsyncRoundedImage(image: String, size: Int, cornerSize: Int) {
    AsyncImage(
        model = image,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(size.dp)
            .clip(RoundedCornerShape(corner = CornerSize(cornerSize.dp)))
    )
}

@Composable
fun ItemButton(textId: Int, enabled: Boolean = true, backgroundColor: Color, corners: Int = 20, padding: Int = 5, contentColor: Color = Color.White, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Button(
            onClick = { onClick.invoke() },
            modifier = Modifier
                .padding(padding.dp),
            shape = RoundedCornerShape(corners.dp),
            enabled = enabled,
            contentPadding = PaddingValues(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                backgroundColor = backgroundColor,
                contentColor = contentColor,
            )
        ) {
            Text(text = stringResource(id = textId))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandedTextField(
    textState: String,
    imageVector: ImageVector,
    modifier: Modifier,
    expanded: Boolean,
    onValueChanged: (String) -> Unit
) {
    var text = textState
    androidx.compose.material3.TextField(
        leadingIcon = {
            androidx.compose.material3.Icon(imageVector = imageVector, null, tint = PrimaryColor)
        },
        value = text,
        colors = androidx.compose.material3.TextFieldDefaults.textFieldColors(
            textColor = Color.Black,
            cursorColor = Color.Black,
            containerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        enabled = false,
        onValueChange = {
            text = it
            onValueChanged(it)
        },
        trailingIcon = {
            ExposedDropdownMenuDefaults.TrailingIcon(
                expanded = expanded
            )
        },
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        singleLine = true
    )
}

@Composable
fun ProfileImageContent(
    imageUrl: String,
    width: Int,
    height: Int
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
            .width(width.dp)
            .height(height.dp)
    )
}

@Composable
fun NoItemsText(textId: Int) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(text = stringResource(id = textId), fontSize = 18.sp)
    }
}

@Composable
fun Modifier.itemBackground(isSelected: Boolean, color: Color): Modifier {
    return this
        .height(40.dp)
        .clip(CircleShape)
        .background(color)
        .then(if (isSelected) Modifier.alpha(0.8f) else Modifier)
}

@Composable
fun BackAndNotificationTopAppBar(navController: NavController) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Image(
                painter = painterResource(R.drawable.ic_back_arrow),
                contentDescription = "Back arrow"
            )
        }

        WhiteItemCard(modifier = Modifier
            .size(50.dp)
            .clickable { navController.navigate(HomeRoute.Notification.route) }) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                BadgedBox(badge = { Badge { Text("6") } }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_icon_notifiaction),
                        contentDescription = "icon",
                        tint = PrimaryColor
                    )
                }
            }
        }
    }
}




