import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.telakuR.easyorder.R
import com.telakuR.easyorder.home.navigation.HomeNavigation
import com.telakuR.easyorder.home.route.HomeRoute
import com.telakuR.easyorder.home.viewModel.HomeVM
import com.telakuR.easyorder.home.viewModel.RequestsVM
import com.telakuR.easyorder.main.enums.RolesEnum
import com.telakuR.easyorder.main.ui.theme.*
import com.telakuR.easyorder.setupProfile.ui.activities.SetUpProfileActivity
import com.telakuR.easyorder.utils.ToastUtils

@Composable
fun BaseHomeScreen(viewModel: RequestsVM = hiltViewModel(), homeVM: HomeVM = hiltViewModel()) {
    val navController = rememberNavController()

    val role = homeVM.currentUserRole.collectAsStateWithLifecycle().value
    val screens = homeVM.getHomeScreens()
    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentDestination = navBackStackEntry?.destination?.route
    var showBottomBar = false

    screens.forEach {
        if(it.route == currentDestination) showBottomBar = true
    }

    Scaffold(
        topBar = {
            HomeTopBar(navController = navController, currentDestination = currentDestination, showBackButton = !showBottomBar)
        },
        bottomBar = {
            HomeBottomBar(showBottomBar = showBottomBar, screens = screens, navController = navController, viewModel = viewModel)
        },
        backgroundColor = Background
    ) {
        Modifier.padding(it)
        HomeNavigation(
            navController = navController,
            role = role
        )

        homeVM.isUserInACompany()
        val isUserInACompany = homeVM.isUserOnACompany.collectAsStateWithLifecycle().value
        if(role == RolesEnum.USER.role && currentDestination == HomeRoute.Home.route && isUserInACompany == true) {
            BottomRightButton(textId = R.string.create_order) {
                homeVM.checkIfUserHasAnOrder()
            }
        }

        val context = LocalContext.current
        val userHasAnOrder = homeVM.hasAlreadyAnOrder.collectAsStateWithLifecycle().value

        LaunchedEffect(userHasAnOrder) {
            if(userHasAnOrder != null) {
                if(!userHasAnOrder) {
                    navController.navigate(HomeRoute.ChooseFastFood.route)
                } else {
                    ToastUtils.showToast(
                        context = context,
                        messageId = R.string.you_have_an_order,
                        length = Toast.LENGTH_SHORT
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeBottomBar(
    showBottomBar: Boolean,
    screens: ArrayList<HomeRoute>,
    navController: NavHostController,
    viewModel: RequestsVM
) {
    if(showBottomBar) {
        WhiteItemCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(10.dp)
        ) {
            BottomBar(screens = screens, navController = navController, viewModel)
        }
    }
}

@Composable
private fun HomeTopBar(
    currentDestination: String?,
    navController: NavHostController,
    showBackButton: Boolean) {
    if (currentDestination != HomeRoute.Notification.route) {
        TopAppBar(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Color.Transparent,
            elevation = 0.dp
        ) {
            if(showBackButton) {
                BackAndNotificationTopAppBar(currentDestination = currentDestination, navController = navController)
            } else {
                NotificationTopAppBar(navController = navController)
            }
        }
    }
}

@Composable
fun NotificationTopAppBar(navController: NavController) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        WhiteItemCard(modifier = Modifier
            .size(50.dp)
            .clickable { navController.navigate(HomeRoute.Notification.route) }) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_icon_notifiaction),
                    contentDescription = "icon",
                    tint = PrimaryColor
                )
            }
        }
    }
}

@Composable
private fun BottomBar(screens: List<HomeRoute>, navController: NavHostController, viewModel: RequestsVM) {
    val navStackBackEntry = navController.currentBackStackEntryAsState().value
    val currentDestination = navStackBackEntry?.destination

    Row(
        modifier = Modifier
            .padding(start = 10.dp, end = 10.dp, top = 8.dp, bottom = 8.dp)
            .background(Color.Transparent)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        screens.forEach { screen ->
            AddItem(
                screen = screen,
                currentDestination = currentDestination,
                navController = navController,
                viewModel = viewModel
            )
        }
    }
}

@Composable
private fun AddItem(
    screen: HomeRoute,
    currentDestination: NavDestination?,
    navController: NavHostController,
    viewModel: RequestsVM
) {
    val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

    val background = if (isSelected) LightGreen else Color.Transparent

    val contentColor = PrimaryColor

    Box(
        modifier = Modifier
            .itemBackground(isSelected, background)
            .clickable(onClick = {
                navController.navigate(screen.route) {
                    popUpTo(navController.graph.findStartDestination().id)
                    launchSingleTop = true
                }
            })
    ) {
        AddItemRow(screen, isSelected, contentColor, viewModel)
    }
}

@Composable
private fun AddItemRow(
    screen: HomeRoute,
    isSelected: Boolean,
    contentColor: Color,
    viewModel: RequestsVM
) {
    Row(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (screen.title == HomeRoute.Requests.title) {
            viewModel.getListOfRequestsFromDB()
            viewModel.getListOfRequestsFromAPI()

            val requestsSize = viewModel.requests.collectAsStateWithLifecycle().value.size

            BadgedBox(badge = { if(requestsSize != 0) Badge { Text(requestsSize.toString()) } }) {
                AddItemIcon(screen, isSelected, contentColor)
            }
        } else {
            AddItemIcon(screen, isSelected, contentColor)
        }

        AnimatedVisibility(visible = isSelected) {
            Text(
                text = screen.title,
                color = Color.Black
            )
        }
    }
}

@Composable
private fun AddItemIcon(screen: HomeRoute, isSelected: Boolean, contentColor: Color) {
    Icon(
        painter = painterResource(id = if (isSelected) screen.icon_focused else screen.icon),
        contentDescription = "icon",
        tint = contentColor
    )
}

@Composable
private fun BottomRightButton(textId: Int, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.BottomEnd,
        modifier = Modifier
            .fillMaxSize()
            .padding(end = 20.dp, bottom = 100.dp)
    ) {
            ItemButton(
                textId = textId,
                backgroundColor = Color.White,
                corners = 6,
                contentColor = PrimaryColor,
                padding = 0
            ) {
                onClick.invoke()
            }
        }
}

@Composable
fun PartOfNoCompanyView() {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 10.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {

        Text(text = stringResource(id = R.string.you_arent_in_any_company), fontSize = 18.sp, textAlign = TextAlign.Center)
        Text(text = stringResource(id = R.string.request_a_company), fontSize = 16.sp, textAlign = TextAlign.Center, color = Color.DarkGray)

        Spacer(modifier = Modifier.height(10.dp))

        val context = LocalContext.current

        MainButton(textId = R.string.join_a_company) {
            context.run {
                startActivity(Intent(context, SetUpProfileActivity::class.java))
            }
        }
    }
}