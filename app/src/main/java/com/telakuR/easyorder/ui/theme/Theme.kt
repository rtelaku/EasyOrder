package com.telakuR.easyorder.ui.theme

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
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
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.telakuR.easyorder.R
import com.telakuR.easyorder.components.autocomplete.AutoCompleteBox
import com.telakuR.easyorder.components.autocomplete.utils.AutoCompleteSearchBarTag
import com.telakuR.easyorder.components.autocomplete.utils.asAutoCompleteEntities
import java.util.*

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
            IconButton(onClick = { navController.popBackStack() }) {
                Image(
                    painter = painterResource(R.drawable.ic_back_arrow),
                    contentDescription = "Back arrow"
                )
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
fun MainButton(text: String, enabled: Boolean = true, onClick: () -> Unit) {
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PictureCardView(painter: Painter, onClick: () -> Unit) {
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
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun SearchBar(items: List<String>) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 15.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        val autoCompleteEntities = items.toList().asAutoCompleteEntities(
            filter = { item, query ->
                item.lowercase(Locale.getDefault())
                    .startsWith(query.lowercase(Locale.getDefault()))
            }
        )

        if (items.isNotEmpty()) {
            AutoCompleteBox(
                items = autoCompleteEntities,
                itemContent = { item ->
                    ValueAutoCompleteItem(item.value)
                }
            ) {
                var value by remember { mutableStateOf("") }
                val view = LocalView.current

                onItemSelected { item ->
                    value = item.value
                    filter(value)
                    view.clearFocus()
                }

                val focusRequester = remember { FocusRequester() }

                TextSearchBar(
                    modifier = Modifier
                        .testTag(AutoCompleteSearchBarTag)
                        .focusRequester(focusRequester)
                        .focusable(),
                    value = value,
                    label = stringResource(id = R.string.where_do_you_work),
                    onDoneActionClick = {
                        view.clearFocus()
                    },
                    onClearClick = {
                        value = ""
                        filter(value)
                        view.clearFocus()
                    },
                    onFocusChanged = { focusState ->
                        if (!focusState.isFocused) {
                            focusRequester.requestFocus()
                        } else {
                            focusRequester.freeFocus()
                        }
                        isSearching = focusState.isFocused
                    },
                    onValueChanged = { query ->
                        value = query
                        filter(value)
                    }
                )
            }
        }
    }
}

@Composable
fun TextSearchBar(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    onDoneActionClick: () -> Unit = {},
    onClearClick: () -> Unit = {},
    onFocusChanged: (FocusState) -> Unit = {},
    onValueChanged: (String) -> Unit
) {
    TextField(
        modifier = modifier
            .fillMaxWidth(.9f)
            .onFocusChanged {
                onFocusChanged(it)
            },
        value = value,
        onValueChange = { query ->
            onValueChanged(query)
        },
        label = { Text(text = label) },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = LightOrangeColor,
            unfocusedLabelColor = OrangeTextColor,
            focusedLabelColor = OrangeTextColor
        ),
        shape = RoundedCornerShape(CornerSize(15.dp)),
        singleLine = true,
        leadingIcon = {
            Image(painter = painterResource(id = R.drawable.ic_icon_search), contentDescription = "Search icon")
        },
        trailingIcon = {
            IconButton(onClick = { onClearClick() }) {
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
fun ValueAutoCompleteItem(item: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)) {
        Text(text = item, fontSize = 12.sp, color = Black)
    }
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
fun ItemButton(text: String, enabled: Boolean = true, backgroundColor: Color, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Button(
            onClick = { onClick.invoke() },
            modifier = Modifier
                .padding(5.dp),
            shape = RoundedCornerShape(20.dp),
            enabled = enabled,
            contentPadding = PaddingValues(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                backgroundColor = backgroundColor,
                contentColor = Color.White
            )
        ) {
            Text(text = text)
        }
    }
}





