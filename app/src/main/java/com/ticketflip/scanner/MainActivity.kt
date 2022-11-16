package com.ticketflip.scanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.hva.amsix.util.Screen
import com.ticketflip.scanner.ui.UIViewModel
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

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TicketflipScannerApp()
                }
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
    scaffoldState: ScaffoldState
) {

    androidx.navigation.compose.NavHost(
        navController,
        startDestination = Screen.ScanAccessScreen.route,

        ) {

        // Access screen
        composable(Screen.ScanAccessScreen.route) {
            Scaffold(
                scaffoldState = scaffoldState,
                content = {
                    /* TOOD */
                }
            )
        }

        // Event screen
        composable(Screen.EventScreen.route) {
            Scaffold(
                scaffoldState = scaffoldState,
                content = {
                    /* TOOD */
                }
            )
        }

        // Profile screen
        composable(Screen.ProfileScreen.route) {
            Scaffold(
                scaffoldState = scaffoldState,
                content = {
                    /* TOOD */
                }
            )
        }

        // Profile screen
        composable(Screen.ProfileScreen.route) {
            Scaffold(
                scaffoldState = scaffoldState,
                content = {
                    /* TOOD */
                }
            )
        }


//        //Read Datapod screen
//        composable(Screen.ReadDatapodScreen.route + "/{datapodId}") { navBackStackEntry ->
//            /* Extracting the id from the route */
//            val datapodId = navBackStackEntry.arguments?.getString("datapodId")
//            /* We check if is null */
//            datapodId?.let {
//                AppShellDatapodRead("Datapod", UIViewModel, scaffoldState) {
//                    ReadDatapodScreen(datapodId = datapodId)
//                }
//            }
//
//        }


        // the rest...
    }

}