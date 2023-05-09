package com.sample.mykotlin.barcode

import android.graphics.RectF
import android.util.Log
import androidx.camera.core.ExperimentalGetImage
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

@ExperimentalGetImage
class BarcodeScanViewModel constructor(
    private val barcodeScanner: BarcodeScanner,
    private val barcodeImageAnalyzer: ImageAnalyzer = BarcodeOptions.provideBarcodeImageAnalyzer(barcodeScanner),
) : ViewModel() {

    init {
        setupImageAnalyzer()
    }

    var toolBarTitleChange = MutableLiveData("BarCode Scan")
    var scanCount = MutableLiveData(0)
    var barCodeStatusListener = MutableLiveData(false)
    var barCodeValue = MutableLiveData ("")
    private var barcodeScanningArea: RectF = RectF()
    private var cameraBoundaryArea: RectF = RectF()
    private val barcodeSizeRateThreshold = 0.2F

    fun updateToolbarName(string: String) {
        toolBarTitleChange.postValue(string)
    }
    fun getBarcodeImageAnalyzer(): ImageAnalyzer {
        return barcodeImageAnalyzer
    }

    fun cameraBoundaryArea(cameraBoundary: RectF) {
        cameraBoundaryArea = cameraBoundary
    }
    fun barcodeScanningArea(barcodeScanning: RectF) {
        barcodeScanningArea = barcodeScanning

    }

    private fun setupImageAnalyzer() {
        barcodeImageAnalyzer.setProcessListener(
            listener = object : ImageAnalyzer.ProcessListenerAdapter() {
                override fun onSucceed(results: List<Barcode>,inputImage: InputImage) {
                    handleBarcodeResults(results,inputImage)
                }
            }
        )
    }

    private fun handleBarcodeResults(results: List<Barcode>,inputImage: InputImage) {
        when (barCodeStatusListener.value) {
            false -> {
                results.forEach { barcode ->
                    val boundaryInfo = getBoundaryInfo(
                        cameraBoundary = cameraBoundaryArea,
                        capturedImageBoundary = RectF(
                            0F,
                            0F,
                            inputImage.width.toFloat(),
                            inputImage.height.toFloat()
                        ),
                        imageRotationDegree = inputImage.rotationDegrees
                    )
                    val boundaryPosition = barcodeBoundaryToGlobalPosition(
                        barcode = barcode,
                        boundaryUiModel = boundaryInfo,
                    )
                    scanningResultByBoundary(boundaryPosition, barcode)
                }
            }
            else -> {}
        }
    }
    
    
        private fun getBoundaryInfo(
        cameraBoundary: RectF,
        capturedImageBoundary: RectF,
        imageRotationDegree: Int,
    ): CoordinatesUiModel {
        return when ((imageRotationDegree / 90) % 2) {
            0 -> { // 0, 180, 360
                val scaleX = cameraBoundary.width() / capturedImageBoundary.width()
                val scaleY = cameraBoundary.height() / capturedImageBoundary.height()
                CoordinatesUiModel(
                    scaleX = scaleX,
                    scaleY = scaleY,
                    offsetX = 0F,
                    offsetY = 50F
                )
            }
            1 -> { // 90, 270
                val scaleX = cameraBoundary.width() / capturedImageBoundary.height()
                val scaleY = cameraBoundary.height() / capturedImageBoundary.width()
                CoordinatesUiModel(
                    scaleX = scaleX,
                    scaleY = scaleY,
                    offsetX = 50F,
                    offsetY = 0F
                )
            }
            else -> CoordinatesUiModel()
        }
    }

    data class CoordinatesUiModel(
        val scaleX: Float = 0F,
        val scaleY: Float = 0F,
        val offsetX: Float = 0F,
        val offsetY: Float = 0F,
    )
    private fun barcodeBoundaryToGlobalPosition(
        barcode: Barcode,
        boundaryUiModel: CoordinatesUiModel,
    ): RectF {
        val barcodeBoundary = barcode.boundingBox
        return when (barcodeBoundary?.isEmpty) {
            false -> {
                RectF(
                    boundaryUiModel.scaleX * (barcodeBoundary.left.toFloat() - boundaryUiModel.offsetX),
                    boundaryUiModel.scaleY * (barcodeBoundary.top.toFloat() - boundaryUiModel.offsetY),
                    boundaryUiModel.scaleX * (barcodeBoundary.right.toFloat() + boundaryUiModel.offsetX),
                    boundaryUiModel.scaleY * (barcodeBoundary.bottom.toFloat() + boundaryUiModel.offsetY),
                )
            }
            else -> RectF()
        }
    }
    private fun checkRectangleInside(smallArea: RectF, largeArea: RectF): Boolean {
        return smallArea.left > largeArea.left &&
                smallArea.top > largeArea.top &&
                smallArea.right < largeArea.right &&
                smallArea.bottom < largeArea.bottom
    }

    private fun checkDistanceMatched(smallArea: RectF, largeArea: RectF): Boolean {
        val rate = (smallArea.width() * smallArea.height()) / (largeArea.width() * largeArea.height())
        return rate > barcodeSizeRateThreshold
    }


    private fun scanningResultByBoundary(
        barcodeGlobalPosition: RectF,
        barcode: Barcode) {
        if( checkRectangleInside(
                barcodeGlobalPosition,
                barcodeScanningArea) ) {
            if( checkDistanceMatched(barcodeGlobalPosition, barcodeScanningArea)){
                Log.d("BARCODEVALUE",barCodeValue.toString())
                barcodeValue(barcode.displayValue.toString())
                scanCountIncrement()
                barCodeScanCompleted(true)
            }
        }

    }
    fun barcodeValue(barcodeValue : String){
        barCodeValue.postValue(barcodeValue)

    }

    fun scanCountIncrement(){
        scanCount.value = scanCount.value?.plus(1)

    }
    fun barCodeScanCompleted(barcodeScanCompleted: Boolean) {
        barCodeStatusListener.postValue(barcodeScanCompleted)
    }

}
