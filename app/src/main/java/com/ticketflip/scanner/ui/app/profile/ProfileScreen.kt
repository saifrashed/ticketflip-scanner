package com.ticketflip.scanner.ui.app.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.ticketflip.scanner.ui.UIViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ProfileScreen(UIViewModel: UIViewModel) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .data("https://www.pngkit.com/png/full/202-2022289_web-reconceptualization-and-redesign-of-carnet-jove-android.png")
                .crossfade(true)
                .build(),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .height(150.dp)
                .width(150.dp).clip(CircleShape)
        )
        Text(
            text = "Saif Rashed",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .padding(5.dp)
        )
        Text(
            text = "saifeddinerashed@icloud.com",
        )
    }
}

