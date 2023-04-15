package com.telakuR.easyorder.home.ui.screens.employeeView

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.telakuR.easyorder.R
import com.telakuR.easyorder.ext.twoDecimalNumber
import com.telakuR.easyorder.home.models.UserInfo
import com.telakuR.easyorder.home.viewModel.OrdersVM
import com.telakuR.easyorder.ui.theme.AsyncRoundedImage
import com.telakuR.easyorder.ui.theme.Background
import com.telakuR.easyorder.ui.theme.PrimaryColor
import com.telakuR.easyorder.ui.theme.WhiteItemCard
import com.telakuR.easyorder.utils.Constants.DEFAULT_PRICE

@Composable
fun PaymentDetailsScreen(
    viewModel: OrdersVM = hiltViewModel(),
    orderId: String
) {
    viewModel.getMyOrderMenu(orderId = orderId)
    val orderDetailsList = viewModel.myOrderMenu.collectAsStateWithLifecycle().value
    val totalPriceByOwner = orderDetailsList
        .groupBy { it.userInfo }
        .mapValues { (_, orderDetailsListForOwner) ->
            orderDetailsListForOwner.sumOf { it.menuItem.price }
        }
        .toList()

    val totalPriceWithoutDelivery = orderDetailsList.sumOf { it.menuItem.price }

    Scaffold(
        content = {
            it
            Column(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Top) {
                    Text(
                        text = stringResource(id = R.string.payment_details),
                        modifier = Modifier.padding(start = 10.dp),
                        fontSize = 25.sp
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp), verticalArrangement = Arrangement.Center
                ) {
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 5.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "")
                        Text(text = stringResource(id = R.string.price), fontSize = 18.sp)
                        Text(text = stringResource(id = R.string.paid), fontSize = 18.sp)
                        Text(text = stringResource(id = R.string.owe), fontSize = 18.sp)
                    }
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(totalPriceByOwner) { (userInfo, toPay) ->
                            PaymentDetailsItem(userInfo = userInfo, toPay = toPay, viewModel = viewModel)
                        }
                    }
                }
            }
        },
        backgroundColor = Background,
        bottomBar = {
            TotalPriceCard(totalPriceWithoutDelivery = totalPriceWithoutDelivery)
        }
    )
}

@Composable
private fun TotalPriceCard(totalPriceWithoutDelivery: Double) {
    val deliveryCharge = remember { mutableStateOf(DEFAULT_PRICE) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(10.dp),
        elevation = 1.dp,
        backgroundColor = PrimaryColor,
        shape = RoundedCornerShape(corner = CornerSize(20.dp))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(id = R.string.sub_total),
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )

                    Text(text = "$totalPriceWithoutDelivery€", color = Color.White, fontSize = 16.sp)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(id = R.string.delivery_charge),
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )

                    PriceTextField(
                        textState = deliveryCharge,
                        contentColor = Color.White,
                        backgroundColor = PrimaryColor,
                        textColor = Color.White
                    )
                }
            }

            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val totalPrice = totalPriceWithoutDelivery + deliveryCharge.value.toDouble()
                    Text(
                        text = stringResource(id = R.string.total),
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )

                    Text(text = "${twoDecimalNumber(totalPrice)}€", color = Color.White, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun PaymentDetailsItem(
    viewModel: OrdersVM,
    userInfo: UserInfo,
    toPay: Double
) {
    val paidTextState = remember { mutableStateOf(DEFAULT_PRICE) }

    WhiteItemCard(modifier = Modifier
        .fillMaxWidth()
        .height(100.dp)
        .padding(horizontal = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AsyncRoundedImage(
                    image = userInfo.picture,
                    size = 45,
                    cornerSize = 10
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(text = userInfo.name, fontWeight = FontWeight.SemiBold)
            }

            Text(text = "$toPay€", fontSize = 16.sp, color = PrimaryColor)
            PriceTextField(
                textState = paidTextState,
                contentColor = PrimaryColor,
                backgroundColor = Color.White,
                textColor = PrimaryColor
            )
            val paid = if(paidTextState.value == DEFAULT_PRICE || paidTextState.value.isEmpty()) toPay.toString() else paidTextState.value
            val oweText = toPay - paid.toDouble()
            Text(text = "${twoDecimalNumber(oweText)}€", fontSize = 16.sp, color = PrimaryColor)
        }
    }

    Spacer(modifier = Modifier.height(10.dp))
}

@Composable
private fun PriceTextField(textState: MutableState<String>, contentColor: Color, backgroundColor: Color, textColor: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            modifier = Modifier.widthIn(min = 30.dp, max = 60.dp),
            value = textState.value,
            onValueChange = { newValue ->
                val doubleNumber = twoDecimalNumber(newValue.toDouble())
                textState.value = doubleNumber
            },
            textStyle = TextStyle(
                textAlign = TextAlign.End,
                color = contentColor,
                background = backgroundColor,
                fontSize = 16.sp
            )
        )

        Text(
            text = "€",
            fontSize = 16.sp,
            color = textColor
        )
    }
}