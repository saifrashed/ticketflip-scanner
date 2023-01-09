package com.ticketflip.scanner.ui.app.event

import android.Manifest
import android.util.Size
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
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
import com.ticketflip.scanner.util.QRCode
import com.ticketflip.scanner.util.TransparentClipLayout
import kotlinx.coroutines.delay


enum class ScanColors(val value: Int) {
    DEFAULT(0x77000000),
    GREEN(0x5000ff00),
    RED(0x50ff0000)
}

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class,
    ExperimentalPermissionsApi::class
)
@Composable
fun EventScanScreen(
    scaffoldState: ScaffoldState,
    UIViewModel: UIViewModel,
    eventId: String,
    eventViewModel: EventViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
) {
    val scanResult by eventViewModel.scanResource.observeAsState()
    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    var maskColor by rememberSaveable { mutableStateOf(ScanColors.DEFAULT) }
    var hasReadCode by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        cameraPermissionState.launchPermissionRequest()
    }

    when (scanResult) {
        is Resource.Success -> {
            isLoading = false

            val scanResponse = (scanResult as Resource.Success<ScanResponse>).data
            if (scanResponse != null) {
                LaunchedEffect(key1 = true) {
                    if (scanResponse.isValid) {
                        maskColor = ScanColors.GREEN
                        UIViewModel.showSnackbar("Ticket is geldig")
                    } else {
                        maskColor = ScanColors.RED
                        UIViewModel.showSnackbar(scanResponse.message)
                    }

                    // Wait 2 seconds before resetting the maskColor
                    delay(2000)
                    maskColor = ScanColors.DEFAULT
                }
            }
        }
        is Resource.Loading -> {
            isLoading = true
        }
        is Resource.Error -> {
            isLoading = false

            LaunchedEffect(key1 = true) {
                maskColor = ScanColors.RED
                UIViewModel.showSnackbar("Er is iets misgegaan")
            }
        }
    }

    Scaffold(
        backgroundColor = MaterialTheme.colorScheme.background,
        scaffoldState = scaffoldState,
        topBar = {
            CenterAlignedTopAppBar(
                title = {

                },
                navigationIcon = {
                    IconButton(onClick = { UIViewModel.goBack(true) }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                },
                actions = {
                    if (hasReadCode && !isLoading) {
                        IconButton(onClick = { hasReadCode = false }) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = "Rescan"
                            )
                        }
                    }
                },
                modifier = Modifier.background(Color.White),
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(0.dp)
        ) {
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
                    color = Color(maskColor.value)
                )
            }

            if (isLoading) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(100F)
                ) {
                    CircularProgressIndicator(
                        color = Color.White
                    )
                }
            }


            val context = LocalContext.current
            val lifecycleOwner = LocalLifecycleOwner.current
            val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

            AndroidView(
                factory = {
                    val previewView = PreviewView(context)

                    previewView.layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    val preview = Preview.Builder().build()
                    val selector = CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build()
                    preview.setSurfaceProvider(previewView.surfaceProvider)
                    val imageAnalysis = ImageAnalysis.Builder()
                        .setTargetResolution(Size(previewView.width, previewView.height))
                        .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST)
                        .build()

                    imageAnalysis.setAnalyzer(
                        ContextCompat.getMainExecutor(context),
                        QRCode { result ->
                            if (!hasReadCode) {
                                eventViewModel.scan(eventId, result)
                                hasReadCode = true
                            }
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
    }
}
