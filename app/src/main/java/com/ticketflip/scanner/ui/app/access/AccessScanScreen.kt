package com.ticketflip.scanner.ui.app.access

import android.Manifest
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Column
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.common.util.concurrent.ListenableFuture
import com.hva.amsix.util.Screen
import com.ticketflip.scanner.data.api.util.Resource
import com.ticketflip.scanner.ui.UIViewModel
import com.ticketflip.scanner.ui.app.UserViewModel
import com.ticketflip.scanner.ui.app.event.CameraPreview
import com.ticketflip.scanner.util.BarCodeAnalyser
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

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


        CameraPreview(userViewModel)

    }



@Composable
fun CameraPreview(userViewModel: UserViewModel) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var preview by remember { mutableStateOf<androidx.camera.core.Preview?>(null) }
    val barCodeVal = remember { mutableStateOf("") }



    AndroidView(
        factory = { AndroidViewContext ->
            PreviewView(AndroidViewContext).apply {
                this.scaleType = PreviewView.ScaleType.FILL_CENTER
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            }
        },

        update = { previewView ->
            val cameraSelector: CameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()
            val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
            val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> =
                ProcessCameraProvider.getInstance(context)

            cameraProviderFuture.addListener({
                preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                val barcodeAnalyser = BarCodeAnalyser { barcodes ->
                    barcodes.forEach { barcode ->
                        barcode.rawValue?.let { barcodeValue ->
                            barCodeVal.value = barcodeValue
                            Toast.makeText(context, barcodeValue, Toast.LENGTH_SHORT).show()

                            userViewModel.setToken(barcodeValue)
                        }
                    }
                }
                val imageAnalysis: ImageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor, barcodeAnalyser)
                    }

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalysis
                    )
                } catch (e: Exception) {
                    Log.d("TAG", "CameraPreview: ${e.localizedMessage}")
                }
            }, ContextCompat.getMainExecutor(context))
        }
    )
}

