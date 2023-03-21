import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.telakuR.easyorder.R
import com.telakuR.easyorder.enums.RolesEnum
import com.telakuR.easyorder.home.navigation.HomeNavigation
import com.telakuR.easyorder.home.route.HomeRoute
import com.telakuR.easyorder.home.viewModel.RequestsVM
import com.telakuR.easyorder.setupProfile.ui.activities.SetUpProfileActivity
import com.telakuR.easyorder.ui.theme.*

@Composable
fun HomeMainView(viewModel: RequestsVM = hiltViewModel()) {
    val navController = rememberNavController()

    val role = viewModel.currentUserRole.collectAsState().value

    val screens = arrayListOf<HomeRoute>()

    if (role == RolesEnum.COMPANY.role) {
        screens.add(HomeRoute.Home)
        screens.add(HomeRoute.Requests)
        screens.add(HomeRoute.Profile)
    } else if (role == RolesEnum.USER.role) {
        screens.add(HomeRoute.Home)
        screens.add(HomeRoute.Order)
        screens.add(HomeRoute.Profile)
    }

    var showBottomBar = false
    val navBackStackEntry = navController.currentBackStackEntryAsState().value

    val currentDestination = navBackStackEntry?.destination?.route

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
private fun HomeTopBar(currentDestination: String?, navController: NavHostController, showBackButton: Boolean) {
    if (currentDestination != HomeRoute.Notification.route) {
        TopAppBar(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Color.Transparent,
            elevation = 0.dp
        ) {
            if(showBackButton) {
                BackAndNotificationTopAppBar(navController = navController)
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
            viewModel.getListOfRequests()

            val requestsSize = viewModel.requests.collectAsState().value.size

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
fun BottomRightButton(textId: Int, onClick: () -> Unit) {
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