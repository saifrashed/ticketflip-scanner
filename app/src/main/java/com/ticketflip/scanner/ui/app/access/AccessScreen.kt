package com.ticketflip.scanner.ui.app.access

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ticketflip.scanner.R
import com.ticketflip.scanner.ui.UIViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun AccessScreen(UIViewModel: UIViewModel) {


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Image(
            painter = painterResource(id = R.drawable.ticketflip_logo),
            contentDescription = "",
            Modifier.width(300.dp)
        )

        Button(
            onClick = {
            },
            shape = RoundedCornerShape(50.dp),
            modifier = Modifier
                .height(50.dp)
                .width(200.dp)
        ) {
            Text(text = "SCAN TOEGANGSCODE")
        }
    }

}

