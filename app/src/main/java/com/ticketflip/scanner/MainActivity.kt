package com.ticketflip.scanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.hva.amsix.util.Constants.EVENTS_ITEM
import com.hva.amsix.util.Constants.PROFILE_ITEM
import com.hva.amsix.util.Screen
import com.hva.amsix.util.SessionManager
import com.ticketflip.scanner.ui.UIViewModel
import com.ticketflip.scanner.ui.app.access.AccessScanScreen
import com.ticketflip.scanner.ui.app.access.AccessScreen
import com.ticketflip.scanner.ui.app.event.EventScanScreen
import com.ticketflip.scanner.ui.app.event.EventScreen
import com.ticketflip.scanner.ui.app.profile.ProfileScreen
import com.ticketflip.scanner.ui.theme.TicketflipscannerTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TicketflipscannerTheme {

                val systemUiController = rememberSystemUiController()
                SideEffect {
                    systemUiController.setStatusBarColor(
                        color = Color.White,
                        darkIcons = true
                    )
                }

                TicketflipScannerApp()

            }
        }
    }
}

@Composable
fun TicketflipScannerApp(UIViewModel: UIViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val navController = rememberNavController()
    val scaffoldState: ScaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    NavHost(navController, UIViewModel, scaffoldState)

    LaunchedEffect(key1 = true) {
        UIViewModel.sharedFlow.collect { event ->
            when (event) {
                is UIViewModel.UIEvents.ShowSnackbar -> {
                    scope.launch {
                        scaffoldState.snackbarHostState.showSnackbar(
                            event.message,
                        )
                    }
                }
                is UIViewModel.UIEvents.Navigate -> {
                    navController.navigate(event.route)
                }
                is UIViewModel.UIEvents.GoBack -> {
                    navController.popBackStack()
                }
            }
        }
    }
}


@Composable
private fun NavHost(
    navController: NavHostController,
    UIViewModel: UIViewModel,
    scaffoldState: ScaffoldState,
) {

    val sessionManager = SessionManager(LocalContext.current)
    var startDestination = Screen.AccessScreen.route

    // check for an existing token and get the user if it exists.
    if (sessionManager.fetchAuthToken()?.isNotBlank() == true) {
        startDestination = Screen.EventScreen.route
    }

    androidx.navigation.compose.NavHost(
        navController,
        startDestination = startDestination,

        ) {

        // Access screen
        composable(Screen.AccessScreen.route) {
            Scaffold(
                scaffoldState = scaffoldState,
                content = {
                    AccessScreen(UIViewModel)
                }
            )
        }

        // Scan Access screen
        composable(Screen.AccessScanScreen.route) {
            AppShellScanner(
                UIViewModel = UIViewModel,
                scaffoldState = scaffoldState,
            ) {
                AccessScanScreen(UIViewModel)
            }
        }

        // Event screen
        composable(Screen.EventScreen.route) {
            AppShell(
                title = stringResource(R.string.event),
                UIViewModel = UIViewModel,
                scaffoldState = scaffoldState,
                showGoBack = false
            ) {
                EventScreen(UIViewModel = UIViewModel)
            }
        }

        // Event Scan screen
        composable(Screen.EventScanScreen.route + "/{eventId}") { navBackStackEntry ->
            /* Extracting the id from the route */
            val eventId = navBackStackEntry.arguments?.getString("eventId")
            /* We check if is null */
            eventId?.let {
                AppShellScanner(
                    UIViewModel = UIViewModel,
                    scaffoldState = scaffoldState,
                ) {
                    EventScanScreen(UIViewModel = UIViewModel, eventId = eventId)
                }
            }
        }

        // Profile screen
        composable(Screen.ProfileScreen.route) {
            AppShell(
                title = stringResource(R.string.profile),
                UIViewModel = UIViewModel,
                scaffoldState = scaffoldState,
                showGoBack = false
            ) {
                ProfileScreen(UIViewModel = UIViewModel)
            }
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AppShell(
    title: String,
    UIViewModel: UIViewModel,
    scaffoldState: ScaffoldState,
    showGoBack: Boolean,
    content: @Composable() () -> Unit
) {

    Scaffold(
        backgroundColor = MaterialTheme.colorScheme.background,
        scaffoldState = scaffoldState,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    if (showGoBack) {
                        IconButton(onClick = { UIViewModel.goBack(true) }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.go_back)
                            )
                        }
                    }
                },
                modifier = Modifier.background(Color.White),
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },

        bottomBar = {
            NavigationBar(modifier = Modifier.fillMaxWidth(), containerColor = Color.White) {
                NavigationBarItem(
                    label = { Text(stringResource(R.string.event)) },
                    selected = UIViewModel.bottomNavIndex.collectAsState().value == EVENTS_ITEM,
                    onClick = {
                        UIViewModel.clickBottomNavItem(EVENTS_ITEM)
                        UIViewModel.navigate(Screen.EventScreen.route)
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Home,
                            contentDescription = stringResource(R.string.event)
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = Color(0x80FFEDCF)
                    )
                )
                NavigationBarItem(
                    label = { Text(stringResource(R.string.profile)) },
                    selected = UIViewModel.bottomNavIndex.collectAsState().value == PROFILE_ITEM,
                    onClick = {
                        UIViewModel.clickBottomNavItem(PROFILE_ITEM)
                        UIViewModel.navigate(Screen.ProfileScreen.route)
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = stringResource(R.string.profile)
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = Color(0x80FFEDCF)
                    )
                )
            }
        }
    ) { innerPadding -> Box(modifier = Modifier.padding(innerPadding)) { content() } }
}


@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AppShellScanner(
    UIViewModel: UIViewModel,
    scaffoldState: ScaffoldState,
    content: @Composable() () -> Unit
) {

    Scaffold(
        backgroundColor = MaterialTheme.colorScheme.background,
        scaffoldState = scaffoldState,
        topBar = {
            CenterAlignedTopAppBar(
                title = {

                },
                navigationIcon = {
                    IconButton(onClick = { UIViewModel.goBack(true) }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.go_back)
                        )
                    }
                },
                modifier = Modifier.background(Color.White),
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
    ) { innerPadding -> Box(modifier = Modifier.padding(innerPadding)) { content() } }
}