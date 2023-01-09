package com.ticketflip.scanner.ui.app.access

import android.Manifest
import android.util.Size
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraSelector.LENS_FACING_BACK
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.hva.amsix.util.Screen
import com.ticketflip.scanner.R
import com.ticketflip.scanner.data.api.util.Resource
import com.ticketflip.scanner.ui.UIViewModel
import com.ticketflip.scanner.ui.app.UserViewModel
import com.ticketflip.scanner.util.QRCode
import com.ticketflip.scanner.util.TransparentClipLayout

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class,
    ExperimentalPermissionsApi::class
)
@Composable
fun AccessScanScreen(
    scaffoldState: ScaffoldState,
    UIViewModel: UIViewModel,
    userViewModel: UserViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {

    val cameraPermissionState =
        rememberPermissionState(permission = Manifest.permission.CAMERA)
    val userResult by userViewModel.userResource.observeAsState()
    var hasReadCode by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }


    LaunchedEffect(key1 = true) {
        cameraPermissionState.launchPermissionRequest()
        UIViewModel.ShowToast("Scan de QR code op de Ticketflip dashboard")
    }

    when (userResult) {
        is Resource.Success -> { // if login is successful we redirect the user to MAIN screen.
            isLoading = false

            LaunchedEffect(key1 = true) {
                UIViewModel.navigate(Screen.EventScreen.route)
                UIViewModel.showSnackbar("Ingelogd")
            }
        }
        is Resource.Loading -> { // if login request is pending.
            isLoading = true
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
                            contentDescription = stringResource(R.string.go_back)

                        )
                    }
                },
                actions = {
                    if (hasReadCode && !isLoading) {
                        IconButton(onClick = { hasReadCode = false }) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = stringResource(R.string.rescan)
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
                    color = Color(0x77000000)
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
                        .requireLensFacing(LENS_FACING_BACK)
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
                                userViewModel.setToken(result)
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




