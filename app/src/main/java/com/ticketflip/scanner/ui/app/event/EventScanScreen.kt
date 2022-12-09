package com.ticketflip.scanner.ui.app.event

import android.Manifest
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
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
import com.ticketflip.scanner.data.api.util.Resource
import com.ticketflip.scanner.data.model.response.ScanResponse
import com.ticketflip.scanner.ui.UIViewModel
import com.ticketflip.scanner.util.QrCodeAnalyzer
import com.ticketflip.scanner.util.TransparentClipLayout
import java.util.*
import kotlin.concurrent.timerTask

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class,
    ExperimentalPermissionsApi::class
)
@Composable
fun EventScanScreen(
    UIViewModel: UIViewModel,
    eventId: String,
    eventViewModel: EventViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val scanResult by eventViewModel.scanResource.observeAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val cameraPermissionState =
            rememberPermissionState(permission = Manifest.permission.CAMERA)

        var scanIndicatorColor by rememberSaveable { mutableStateOf(0x77000000) }

        LaunchedEffect(key1 = true) {
            cameraPermissionState.launchPermissionRequest()
//            eventViewModel.scan(eventId, "638a26d3d98ad7258a4ace80")
        }


        when (scanResult) {
            is Resource.Success -> {
                (scanResult as Resource.Success<ScanResponse>).data?.let {

                    when (it.isValid) { // visualize status in clip.
                        false -> {
                            // if ticket is not valid
                            LaunchedEffect(key1 = true) {
                                scanIndicatorColor = 0x50ff0000

                                Timer().schedule(timerTask {
                                    scanIndicatorColor = 0x77000000
                                }, 2000)

                                UIViewModel.showSnackbar(
                                    it.message
                                )
                            }
                        }
                        true -> {
                            // if ticket is valid
                            LaunchedEffect(key1 = true) {
                                scanIndicatorColor = 0x5000ff00

                                Timer().schedule(timerTask {
                                    scanIndicatorColor = 0x77000000
                                }, 2000)

                                UIViewModel.showSnackbar(
                                    it.message
                                )
                            }
                        }
                    }
                }
            }
            is Resource.Error -> {
                // if ticket is not valid
                LaunchedEffect(key1 = true) {
                    scanIndicatorColor = 0x50ff0000

                    Timer().schedule(timerTask {
                        scanIndicatorColor = 0x77000000
                    }, 2000)

                    UIViewModel.showSnackbar("Er is iets misgegaan")
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
                    color = Color(scanIndicatorColor)
                )
            }
            CameraPreview(eventViewModel, eventId)
        }
    }
}


@Composable
fun CameraPreview(eventViewModel: EventViewModel, eventId: String) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    AndroidView(
        factory = { context ->
            val previewView = PreviewView(context)
            val preview = Preview.Builder().build()
            val selector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()
            preview.setSurfaceProvider(previewView.surfaceProvider)
            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(previewView.width, previewView.height))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
            imageAnalysis.setAnalyzer(
                ContextCompat.getMainExecutor(context),
                QrCodeAnalyzer { result ->
                    eventViewModel.scan(eventId, result)
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
