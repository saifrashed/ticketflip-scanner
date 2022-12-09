package com.ticketflip.scanner.ui.app.access

import android.Manifest
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraSelector.LENS_FACING_BACK
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.hva.amsix.util.Screen
import com.ticketflip.scanner.data.api.util.Resource
import com.ticketflip.scanner.ui.UIViewModel
import com.ticketflip.scanner.ui.app.UserViewModel
import com.ticketflip.scanner.util.QrCodeAnalyzer
import com.ticketflip.scanner.util.TransparentClipLayout


@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class,
    ExperimentalPermissionsApi::class
)
@Composable
fun AccessScanScreen(
    UIViewModel: UIViewModel,
    userViewModel: UserViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {

    val cameraPermissionState =
        rememberPermissionState(permission = Manifest.permission.CAMERA)
    val userResult by userViewModel.userResource.observeAsState()

    LaunchedEffect(key1 = true) {
        cameraPermissionState.launchPermissionRequest()
//        userViewModel.setToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjYxYjg5M2QzZTE2YWFlMDAxNjBlMTRmNyIsImlhdCI6MTY2OTQ1Njg1N30.6wvRw0SuYhSzN2xyalWtmF-APtOVllEcvem73xL4cs4")
    }

    when (userResult) {
        is Resource.Success -> { // if login is successful we redirect the user to MAIN screen.
            LaunchedEffect(key1 = true) {
                UIViewModel.navigate(Screen.EventScreen.route)
                UIViewModel.showSnackbar("Login gelukt!")
            }
        }
    }


    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(100F)
        ) {
            TransparentClipLayout(
                modifier = Modifier.fillMaxSize(),
                width = 200.dp,
                height = 200.dp,
                offsetY = 150.dp,
                color = Color(0x77000000)
            )
        }
        CameraPreview(userViewModel)
    }
}


@Composable
fun CameraPreview(userViewModel: UserViewModel) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    AndroidView(
        factory = { context ->
            val previewView = PreviewView(context)
            val preview = Preview.Builder().build()
            val selector = CameraSelector.Builder()
                .requireLensFacing(LENS_FACING_BACK)
                .build()
            preview.setSurfaceProvider(previewView.surfaceProvider)
            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(previewView.width, previewView.height))
                .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST)
                .build()
            imageAnalysis.setAnalyzer(
                ContextCompat.getMainExecutor(context),
                QrCodeAnalyzer { result ->
                    userViewModel.setToken(result)
                }
            )
            try {
                cameraProviderFuture.get().bindToLifecycle(
                    lifecycleOwner,
                    selector,
                    preview,
                    imageAnalysis
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
            previewView
        }
    )
}




