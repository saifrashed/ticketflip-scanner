package com.ticketflip.scanner.ui.app.event

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.hva.amsix.util.Screen
import com.ticketflip.scanner.ui.UIViewModel
import com.ticketflip.scanner.ui.app.UserViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun EventScreen(
    UIViewModel: UIViewModel,
    userViewModel: UserViewModel,
    eventViewModel: EventViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
) {

    val eventList by eventViewModel.eventResource.observeAsState()

    LaunchedEffect(key1 = true) {
        userViewModel.token?.let { eventViewModel.getEvents(it) }
    }

    LazyColumn {
        items(eventList?.data?.size ?: 0) { index ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Card(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    onClick = { /* TODO */ }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 0.dp, vertical = 4.dp),
                                verticalArrangement = Arrangement.Center
                            ) {
                                eventList?.data?.get(index)?.let {
                                    Text(
                                        text = it.eventName,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                Text(
                                    text = "0 members",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            IconButton(
                                onClick = { /*TODO*/ },
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

                            Text(
                                text = "Beschrijving",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp),
                            )

                            Text(
                                text =  eventList?.data?.get(index)?.eventDescription ?: "",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )

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
                                    Text(text = "Scannen")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}

