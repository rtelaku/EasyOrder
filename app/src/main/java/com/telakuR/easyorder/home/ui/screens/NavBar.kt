import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
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
import com.telakuR.easyorder.ui.theme.Background
import com.telakuR.easyorder.ui.theme.LightGreen
import com.telakuR.easyorder.ui.theme.PrimaryColor
import com.telakuR.easyorder.ui.theme.WhiteItemCard

@Composable
fun HomeView() {
    val navController = rememberNavController()

    val role = RolesEnum.COMPANY.role

    val screens = arrayListOf<HomeRoute>()

    if (role == RolesEnum.COMPANY.role) {
        screens.add(HomeRoute.Home)
        screens.add(HomeRoute.Report)
        screens.add(HomeRoute.Profile)
    } else if (role == RolesEnum.USER.role) {
        screens.add(HomeRoute.Home)
        screens.add(HomeRoute.Order)
        screens.add(HomeRoute.Profile)
    }

    var showBottomBar = false
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val currentDestination = navBackStackEntry?.destination?.route

    screens.forEach {
        if(it.route == currentDestination) showBottomBar = true
    }

    Scaffold(
        topBar = {
            if (currentDestination != HomeRoute.Notification.route) {
                TopAppBar(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color.Transparent,
                    elevation = 0.dp
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.End
                    ) {
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
            }
        },
        bottomBar = {
            if(showBottomBar) {
                WhiteItemCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(10.dp)
                ) {
                    BottomBar(screens = screens, navController = navController)
                }
            }
        },
        backgroundColor = Background
    ) {
        Modifier.padding(it)
        HomeNavigation(
            navController = navController
        )
    }
}

@Composable
fun BottomBar(screens: List<HomeRoute>, navController: NavHostController) {

    val navStackBackEntry by navController.currentBackStackEntryAsState()
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
                navController = navController
            )
        }
    }
}

@Composable
fun RowScope.AddItem(
    screen: HomeRoute,
    currentDestination: NavDestination?,
    navController: NavHostController
) {
    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

    val background = if (selected) LightGreen else Color.Transparent

    val contentColor = PrimaryColor

    Box(
        modifier = Modifier
            .height(40.dp)
            .clip(CircleShape)
            .background(background)
            .clickable(onClick = {
                navController.navigate(screen.route) {
                    popUpTo(navController.graph.findStartDestination().id)
                    launchSingleTop = true
                }
            })
    ) {
        Row(
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {


            //* if menu title : Report means we will show badge
            if (screen.title == HomeRoute.Report.title) { // with badge
                BadgedBox(badge = { Badge { Text("6") } }) {
                    Icon(
                        painter = painterResource(id = if (selected) screen.icon_focused else screen.icon),
                        contentDescription = "icon",
                        tint = contentColor
                    )
                }

            } else {
                Icon(
                    painter = painterResource(id = if (selected) screen.icon_focused else screen.icon),
                    contentDescription = "icon",
                    tint = contentColor
                )

            }

            AnimatedVisibility(visible = selected) {
                Text(
                    text = screen.title,
                    color = Color.Black
                )
            }
        }
    }
}