package com.example.real_madrid_museo.ui.ScannerScreen

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.real_madrid_museo.home.MadridBlue
import com.example.real_madrid_museo.home.MadridGold
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors
import androidx.compose.ui.graphics.drawscope.Stroke

@OptIn(ExperimentalGetImage::class)
@Composable
fun ScannerScreen(onResultFound: (String) -> Unit) { // <-- CAMBIO: Ahora recibe un String genérico
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    var hasScanned by remember { mutableStateOf(false) }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (hasCameraPermission) {
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }
                },
                modifier = Modifier.fillMaxSize(),
                update = { previewView ->
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()

                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                        val imageAnalysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()

                        val scanner = BarcodeScanning.getClient()

                        imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                            val mediaImage = imageProxy.image

                            if (mediaImage != null && !hasScanned) {
                                val image = InputImage.fromMediaImage(
                                    mediaImage,
                                    imageProxy.imageInfo.rotationDegrees
                                )

                                scanner.process(image)
                                    .addOnSuccessListener { barcodes ->
                                        for (barcode in barcodes) {
                                            val value = barcode.rawValue ?: continue

                                            // ESCALABILIDAD:
                                            // No preguntamos si es 7. Simplemente avisamos lo que hay.
                                            hasScanned = true
                                            onResultFound(value)
                                        }
                                    }
                                    .addOnCompleteListener {
                                        imageProxy.close()
                                    }
                            } else {
                                imageProxy.close()
                            }
                        }

                        val cameraSelector = CameraSelector.Builder()
                            .requireLensFacing(lensFacing)
                            .build()

                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                imageAnalysis
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }, ContextCompat.getMainExecutor(context))
                }
            )

            QRScannerOverlay()

            FloatingActionButton(
                onClick = {
                    lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK)
                        CameraSelector.LENS_FACING_FRONT
                    else
                        CameraSelector.LENS_FACING_BACK
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 110.dp, end = 24.dp),
                containerColor = MadridGold,
                contentColor = MadridBlue,
                shape = CircleShape
            ) {
                Icon(Icons.Default.FlipCameraAndroid, contentDescription = "Girar Cámara")
            }

        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("El permiso de cámara es necesario", color = MadridBlue)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                    Text("Conceder Permiso")
                }
            }
        }
    }
}

@Composable
fun QRScannerOverlay() {
    Canvas(modifier = Modifier.fillMaxSize().graphicsLayer(alpha = 0.99f)) {
        val boxSize = 250.dp.toPx()
        val left = (size.width - boxSize) / 2
        val top = (size.height - boxSize) / 2

        drawRect(Color.Black.copy(alpha = 0.6f))
        drawRoundRect(
            color = Color.Transparent,
            topLeft = Offset(left, top),
            size = Size(boxSize, boxSize),
            cornerRadius = CornerRadius(20.dp.toPx()),
            blendMode = BlendMode.Clear
        )
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(left, top),
            size = Size(boxSize, boxSize),
            cornerRadius = CornerRadius(20.dp.toPx()),
            style = Stroke(width = 3.dp.toPx())
        )
    }
}