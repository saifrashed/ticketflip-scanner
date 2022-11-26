package com.ticketflip.scanner.ui.app.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.hva.amsix.util.Screen
import com.ticketflip.scanner.R
import com.ticketflip.scanner.data.api.util.Resource
import com.ticketflip.scanner.ui.UIViewModel
import com.ticketflip.scanner.ui.app.UserViewModel
import com.ticketflip.scanner.util.ConnectionState
import com.ticketflip.scanner.util.connectivityState
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class,
    ExperimentalCoroutinesApi::class
)
@Composable
fun ProfileScreen(
    UIViewModel: UIViewModel,
    userViewModel: UserViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {

    val user by userViewModel.userResource.observeAsState()

    val connection by connectivityState()
    val isConnected = connection === ConnectionState.Available

    if (isConnected) {
        when (user) {
            is Resource.Success -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_baseline_person_24),
                        contentDescription = "GEBRUIKER"
                    )
                    Text(
                        text = (userViewModel.userResource.value?.data?.firstName) + " " + (userViewModel.userResource.value?.data?.lastName),
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier
                            .padding(5.dp)
                    )
                    userViewModel.userResource.value?.data?.let {
                        Text(
                            text = it.userEmail,
                        )
                    }

                    Button(
                        onClick = {
                            userViewModel.clearToken()
                            UIViewModel.navigate(Screen.AccessScreen.route)
                        },
                        shape = RoundedCornerShape(50.dp),
                        modifier = Modifier
                            .padding(0.dp, 25.dp)
                            .height(50.dp)
                            .width(200.dp)
                    ) {
                        Text(text = "Uitloggen")
                    }
                }
            }
            is Resource.Loading -> {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
            is Resource.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            userViewModel.clearToken()
                            UIViewModel.navigate(Screen.AccessScreen.route)
                        },
                        shape = RoundedCornerShape(50.dp),
                        modifier = Modifier
                            .padding(0.dp, 25.dp)
                            .height(50.dp)
                            .width(200.dp)
                    ) {
                        Text(text = "Uitloggen")
                    }
                }
            }
            is Resource.Empty -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = {
                                userViewModel.clearToken()
                                UIViewModel.navigate(Screen.AccessScreen.route)
                            },
                            shape = RoundedCornerShape(50.dp),
                            modifier = Modifier
                                .padding(0.dp, 25.dp)
                                .height(50.dp)
                                .width(200.dp)
                        ) {
                            Text(text = "Uitloggen")
                        }
                    }
                }
            }
            else -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            userViewModel.clearToken()
                            UIViewModel.navigate(Screen.AccessScreen.route)
                        },
                        shape = RoundedCornerShape(50.dp),
                        modifier = Modifier
                            .padding(0.dp, 25.dp)
                            .height(50.dp)
                            .width(200.dp)
                    ) {
                        Text(text = "Uitloggen")
                    }
                }
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_baseline_signal_wifi_off_24),
                contentDescription = "GEEN INTERNET VERBINDING"
            )
            Text(
                text = "GEEN INTERNET VERBINDING",
            )
        }
    }
}

