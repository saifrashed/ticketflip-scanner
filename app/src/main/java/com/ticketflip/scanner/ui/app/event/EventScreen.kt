package com.ticketflip.scanner.ui.app.event

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.hva.amsix.util.Constants
import com.hva.amsix.util.Screen
import com.ticketflip.scanner.R
import com.ticketflip.scanner.data.api.util.Resource
import com.ticketflip.scanner.ui.UIViewModel
import com.ticketflip.scanner.util.ConnectionState
import com.ticketflip.scanner.util.connectivityState
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class,
    ExperimentalCoroutinesApi::class
)
@Composable
fun EventScreen(
    UIViewModel: UIViewModel,
    eventViewModel: EventViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
) {

    val eventList by eventViewModel.eventResource.observeAsState()
    val context = LocalContext.current
    val connection by connectivityState()
    val isConnected = connection === ConnectionState.Available

    if (isConnected) {
        when (eventList) {
            is Resource.Success -> {
                LazyColumn {
                    items(eventList?.data?.size ?: 0) { index ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
                        ) {
                            Card(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                                elevation = 0.dp
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(20.dp, 10.dp)
                                    ) {

                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(0.dp, 10.dp),
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            eventList?.data?.get(index)?.let {
                                                Text(
                                                    text = it.eventName,
                                                    fontWeight = FontWeight.Bold,
                                                    style = MaterialTheme.typography.bodyLarge
                                                )
                                            }
                                        }
                                        IconButton(
                                            onClick = {
                                                eventList?.data?.get(index)
                                                    ?.let { linkToWebpage(context, it.eventId) }
                                            },
                                            modifier = Modifier
                                                .clip(CircleShape)
                                                .background(Color.White)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.Info,
                                                contentDescription = "Favorite",
                                            )
                                        }
                                    }

                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .memoryCachePolicy(CachePolicy.ENABLED)
                                            .data(eventList?.data?.get(index)?.eventImage ?: "")
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = "",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(150.dp)
                                    )

                                    Column(modifier = Modifier.padding(20.dp)) {

                                        eventList?.data?.get(index)?.eventDescription?.let {
                                            Text(
                                                text = it,
                                                style = MaterialTheme.typography.bodyMedium,
                                                modifier = Modifier.padding(
                                                    top = 12.dp,
                                                    bottom = 8.dp
                                                ),
                                            )
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            Button(
                                                onClick = {
                                                    eventList?.data?.get(index)?.let {
                                                        Screen.EventScanScreen.withArgs(
                                                            it.eventId
                                                        )
                                                    }?.let {
                                                        UIViewModel.navigate(
                                                            it
                                                        )
                                                    }
                                                },
                                                shape = RoundedCornerShape(50.dp),
                                                modifier = Modifier
                                                    .height(50.dp)
                                                    .width(125.dp)
                                            ) {
                                                Text(text = stringResource(R.string.scan))
                                            }
                                        }
                                    }
                                }
                            }
                        }
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
            is Resource.Empty -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_baseline_event_busy_24),
                        contentDescription = stringResource(R.string.no_upcoming_events)
                    )
                    Text(
                        text = stringResource(R.string.no_upcoming_events),
                    )
                }
            }
            else -> {
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
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_baseline_signal_wifi_off_24),
                contentDescription = stringResource(R.string.no_internet_connection)
            )
            Text(
                text = stringResource(R.string.no_internet_connection),
            )
        }
    }
}

fun linkToWebpage(context: Context, id: String) {
//val context = ContextAmbient.current
    val openURL = Intent(Intent.ACTION_VIEW)
    openURL.data = Uri.parse(Constants.BASE_URL + "shop/event?id=" + id)
    startActivity(context, openURL, null)
}