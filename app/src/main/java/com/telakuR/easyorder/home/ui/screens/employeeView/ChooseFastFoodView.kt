package com.telakuR.easyorder.home.ui.screens.employeeView

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.telakuR.easyorder.R
import com.telakuR.easyorder.home.models.FastFood
import com.telakuR.easyorder.home.route.HomeRoute
import com.telakuR.easyorder.home.viewModel.OrdersVM
import com.telakuR.easyorder.main.ui.theme.AsyncRoundedImageFromUrl
import com.telakuR.easyorder.main.ui.theme.Background
import com.telakuR.easyorder.main.ui.theme.SearchBar
import com.telakuR.easyorder.main.ui.theme.WhiteItemCard
import com.telakuR.easyorder.utils.Constants.FAST_FOOD_ID
import java.util.*

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ChooseFastFoodScreen(navController: NavController, viewModel: OrdersVM = hiltViewModel()) {
    viewModel.getFastFoods()
    val fastFoods = viewModel.fastFoods.collectAsStateWithLifecycle().value

    Scaffold(
        topBar = {
            TopAppBar()
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
                    FastFoodList(fastFoods = fastFoods, textState = textState, navController = navController)
                }
            }
        },
        backgroundColor = Background
    )
}

@Composable
private fun TopAppBar() {
    Column(modifier = Modifier.fillMaxWidth(0.5f)) {
        Text(
            text = stringResource(R.string.select_fastfood),
            modifier = Modifier
                .padding(start = 10.dp),
            fontSize = 25.sp
        )
    }
}

@Composable
private fun FastFoodList(fastFoods: List<FastFood>, textState: MutableState<TextFieldValue>, navController: NavController) {
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
            val fastFood =  fastFoods[index]

            WhiteItemCard(modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)) {
                Column(modifier = Modifier
                    .height(200.dp)
                    .padding(horizontal = 5.dp, vertical = 10.dp)
                    .clickable {
                        navController.navigate(HomeRoute.ChooseFood.route + "/?$FAST_FOOD_ID=${fastFood.id}")
                    },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceAround) {

                    AsyncRoundedImageFromUrl(image = fastFood.picture, size = 80, cornerSize = 5)

                    Text(text = fastFood.name, textAlign = TextAlign.Center)
                }
            }
        }
    }
}
