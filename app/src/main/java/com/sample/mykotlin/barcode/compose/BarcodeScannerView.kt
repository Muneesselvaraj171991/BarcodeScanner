package com.sample.mykotlin.barcode.compose


import android.content.Context
import android.content.Intent
import android.graphics.PointF
import android.graphics.RectF
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Badge
import androidx.compose.material.BadgedBox
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter

import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.sample.mykotlin.MainActivity
import com.sample.mykotlin.R
import com.sample.mykotlin.barcode.BarcodeScanViewModel
import com.sample.mykotlin.barcode.ImageAnalyzer
import com.sample.mykotlin.barcode.getCameraXProvider

import com.sample.mykotlin.ui.theme.black
import com.sample.mykotlin.ui.theme.colorPrimary
import com.sample.mykotlin.ui.theme.everGreen
import com.sample.mykotlin.ui.theme.white
import kotlinx.coroutines.delay


@ExperimentalGetImage
@Composable
fun BarcodeScannerView(
    imageAnalyzer: ImageAnalyzer,
    viewModel : BarcodeScanViewModel

) {
    val barcodeScanCompleted by viewModel.barCodeStatusListener.observeAsState(false)

    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val configuration = LocalConfiguration.current
    var camera by remember { mutableStateOf<Camera?>(null) }


    val toolbarName: String? = viewModel.toolBarTitleChange.observeAsState().value
    val barcodeValue: String? = viewModel.barCodeValue.observeAsState().value


    val cameraPreview = Preview.Builder().build()

    val imageAnalysis = ImageAnalysis.Builder()
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()

    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    val previewWidget = remember { PreviewView(context) }

    suspend fun setupCameraPreview() {
        val cameraProvider = context.getCameraXProvider()
        cameraProvider.unbindAll()
        camera = cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            cameraPreview,
            imageAnalysis,
        )
        cameraPreview.setSurfaceProvider(previewWidget.surfaceProvider)
        imageAnalysis.setAnalyzer(imageAnalyzer.getAnalyzerExecutor(), imageAnalyzer)
    }

    LaunchedEffect(key1 = configuration) {
        setupCameraPreview()
    }


    LaunchedEffect(key1 = barcodeScanCompleted) {
        if (barcodeScanCompleted) {
            try {
                waitingTime(2000) {
                    viewModel.barCodeScanCompleted(false)
                    viewModel.barcodeValue("")
                }
            } catch (e: Exception) {
                Log.d("Exception==","waitingTimeError")
            }

        }

    }

    Box(modifier = Modifier.fillMaxSize()) {
        val width = remember { mutableStateOf(0) }
        val height = remember { mutableStateOf(0) }

        AndroidView(
            factory = { previewWidget },
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned { layoutCoordinates ->
                    width.value = layoutCoordinates.size.width
                    height.value = layoutCoordinates.size.height

                }
        )

        //CameraBoundaryArea
        viewModel.cameraBoundaryArea(RectF(
            0F,
            0F,
            width.value.toFloat(),
            height.value.toFloat()
        ))


        //calculateBarcodeScanningAreaRect
        fun calculateBarcodeScanningArea(size: Int, centerPoint: PointF): RectF {
            val scanningAreaSize = size * 0.8F
            val left = centerPoint.x - scanningAreaSize * 0.5F
            val top = centerPoint.y - scanningAreaSize * 0.5F
            val right = centerPoint.x + scanningAreaSize * 0.5F
            val bottom = centerPoint.y + scanningAreaSize * 0.1F
            return RectF(left, top, right, bottom)
        }

        val centerPoint = PointF(width.value* 0.5F, height.value * 0.5F)
        val scanningBarcodeArea = calculateBarcodeScanningArea(size = minOf(width.value, height.value), centerPoint)
        viewModel.barcodeScanningArea(scanningBarcodeArea)


        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
        ) {

            camera?.let {
                BottomBar(it,viewModel,toolbarName.toString(),barcodeValue.toString())

            }
        }
        toolbarName?.let { CustomScanTopBar(context,it) }


        Image(
            painter = painterResource(id = R.drawable.rectangle_img),
            modifier = Modifier
                .padding(
                    dimensionResource(id = R.dimen.fontsize_8dp), bottom = dimensionResource(
                        id = R.dimen.paddingBottom
                    )
                )
                .align(Alignment.Center)
                .scale(scale),
            contentDescription = ""
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(bottom = dimensionResource(id = R.dimen. paddingBottom))
        ) {

            if(barcodeValue.toString().isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.img_bar_code),
                            modifier = Modifier.padding(dimensionResource(id = R.dimen.fontsize_8dp)),
                            contentDescription = ""
                        )

                    }
                    Text(
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = dimensionResource(id = R.dimen.fontsize_10dp)),
                        text = stringResource(id = R.string.aim_camera),
                        style = MaterialTheme.typography.subtitle2.copy(color = white)

                    )

                }
            }
        }
        BackHandler {
            onBackPressed(context)
        }
    }
}

fun onBackPressed(context :Context){
    val intent = Intent(context, MainActivity::class.java)
    context.startActivity(intent)
}


@Composable
fun CustomScanTopBar(context: Context,toolbarName: String) {
    TopAppBar(
        elevation = dimensionResource(id = R.dimen.elevation),
        modifier = Modifier
            .fillMaxWidth()
            .background(black.copy(alpha = 0.6f)),
        title = {


            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = toolbarName,
                    modifier = Modifier.align(Alignment.Center),
                    color = white,
                    fontSize = dimensionResource(id = R.dimen.subtext_fontsize).value.sp

                )

                IconButton(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    onClick = {
                        onBackPressed(context)
                    }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.cancel),
                        contentDescription = stringResource(id = R.string.canel)
                    )
                }
            }
        },
    )
}

@Composable
fun BottomBar(camera:Camera, viewModel: BarcodeScanViewModel, toolbarName: String,barcodeValue:String) {
    var flashEnabled by remember { mutableStateOf(false) }
    val scanCount by viewModel.scanCount.observeAsState(0)
    Box(modifier = Modifier.fillMaxWidth()) {

        Row(horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.padding_))
                .fillMaxWidth()


        ) {

            IconButton(
                modifier = Modifier.align(Alignment.CenterVertically),

                onClick = {
                    if (flashEnabled) {
                        flashEnabled = false
                        camera.cameraControl.enableTorch(flashEnabled)
                    } else {
                        flashEnabled = true
                        camera.cameraControl.enableTorch(flashEnabled)

                    }
                }
            ) {

                Image(
                    painter = painterResource(
                        id = if (flashEnabled) {
                            R.drawable.flashlight_on
                        } else {
                            R.drawable.flashlight_off
                        }
                    ),
                    contentDescription = stringResource(id = R.string.flash_light),

                )
            }


            if(toolbarName == stringResource(id = R.string.barcode_scan)){

                BadgedBox(
                    badge = {
                        colorPrimary
                        if(scanCount!=0) {
                            Badge(
                                backgroundColor= colorPrimary
                            ) {
                                Text(text = "$scanCount",color= white)
                            }
                        }
                    }
                ){

                    Button(
                        colors = ButtonDefaults.buttonColors(backgroundColor = white),
                        shape = RoundedCornerShape(dimensionResource(id = R.dimen.fontsize_16dp)),
                        onClick = {
                        }

                    ) {

                        Text(
                            text = stringResource(id = R.string.to_cart),
                            style = MaterialTheme.typography.subtitle2.copy(color = everGreen)
                        )
                        Spacer(modifier = Modifier.width(width = dimensionResource(id = R.dimen.fontsize_8dp)))
                        Image(
                            painter = painterResource(id = R.drawable.arrow),
                            contentDescription = stringResource(id = R.string.arrowIcon)
                        )

                    }}
            }

        }


        if(barcodeValue.isNotEmpty()) {
            CustomSnackBar(barcodeValue)
        }
    }
    BottomSheet(viewModel)
}


suspend fun waitingTime(time: Long, onEnd: () -> Unit) {
    delay(timeMillis = time)
    onEnd()
}
