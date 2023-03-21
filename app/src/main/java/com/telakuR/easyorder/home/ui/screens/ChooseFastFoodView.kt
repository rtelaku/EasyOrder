package com.telakuR.easyorder.home.ui.screens

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.telakuR.easyorder.R
import com.telakuR.easyorder.home.models.FastFood
import com.telakuR.easyorder.home.viewModel.OrdersVM
import com.telakuR.easyorder.ui.theme.*
import java.util.*

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ChooseFastFoodScreen(navController: NavController, viewModel: OrdersVM = hiltViewModel()) {
    viewModel.getFastFoods()
    val fastFoods = viewModel.fastFoods.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(navController = navController)
        },
        content = {
            it
            Column(modifier = Modifier.fillMaxSize()) {
                val textState = remember { mutableStateOf(TextFieldValue("")) }

                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Top) {
                    val fastFoodsByName = fastFoods.map { it.name }
                    SearchBar(
                        searchTextId = R.string.search_fastFood,
                        items = fastFoodsByName,
                        textState = textState
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center
                ) {
                    if (fastFoods.isEmpty()) {
                        NoItemsText(textId = R.string.no_fast_foods_available)
                    } else {
                        FastFoodList(fastFoods, textState)
                    }
                }
            }
        },
        backgroundColor = Background
    )

}

@Composable
private fun TopAppBar(navController: NavController) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Toolbar(navController = navController)

        Text(
            text = stringResource(R.string.select_fastfood),
            modifier = Modifier
                .padding(start = 10.dp),
            fontSize = 25.sp
        )
    }
}

@Composable
private fun FastFoodList(fastFoods: List<FastFood>, textState: MutableState<TextFieldValue>) {
    var filteredFastFoods: List<FastFood>

    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
        val searchedText = textState.value.text

        filteredFastFoods = if (searchedText.isEmpty()) {
            fastFoods
        } else {
            val resultList = ArrayList<FastFood>()
            for (fastFood in fastFoods) {
                if (fastFood.name.lowercase(Locale.getDefault())
                        .contains(searchedText.lowercase(Locale.getDefault()))
                ) {
                    resultList.add(fastFood)
                }
            }
            resultList
        }

        items(filteredFastFoods.size) { index ->
            WhiteItemCard(modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)) {
                Column(modifier = Modifier
                    .height(200.dp)
                    .padding(horizontal = 5.dp, vertical = 10.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceAround) {
                    val fastFood =  fastFoods[index]

                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(fastFood.picture)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .clip(RoundedCornerShape(5.dp))
                            .size(80.dp)
                    )

                    Text(text = fastFood.name, textAlign = TextAlign.Center)
                }
            }
        }
    }
}
