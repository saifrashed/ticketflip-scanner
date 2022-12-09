package com.ticketflip.scanner.ui.app.event

import android.Manifest
import android.util.Log
import android.view.ViewGroup
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
import com.google.common.util.concurrent.ListenableFuture
import com.ticketflip.scanner.data.api.util.Resource
import com.ticketflip.scanner.data.model.response.ScanResponse
import com.ticketflip.scanner.ui.UIViewModel
import com.ticketflip.scanner.util.BarCodeAnalyser
import com.ticketflip.scanner.util.TransparentClipLayout
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
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
    var preview by remember { mutableStateOf<Preview?>(null) }
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
        modifier = Modifier
            .fillMaxSize(),
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

                            eventViewModel.scan(eventId, barcodeValue)

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

