package com.sample.mykotlin.barcode

import android.util.Log
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@ExperimentalGetImage
class ImageAnalyzer constructor(
    private val barcodeScanner: BarcodeScanner
) : ImageAnalysis.Analyzer {

    private val executor = Executors.newSingleThreadExecutor()
    private var processing = false
    private var processListener: ProcessListener? = null
    private var lastAnalyzedTimeStamp = 0L
    override fun analyze(imageProxy: ImageProxy) {
        val currentTimestamp = System.currentTimeMillis()
        if (currentTimestamp - lastAnalyzedTimeStamp >= TimeUnit.SECONDS.toMillis(1)) {

            val mediaImage = imageProxy.image ?: return
            if (processing) return

            val inputImage =
                InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            processing = true
            barcodeScanner.process(inputImage)
                .addOnSuccessListener { results ->
                    if (results.isNotEmpty()) {
                        processListener?.onSucceed(results,inputImage)

                    } else {
                        Log.d("TAG", "analyze: No barcode Scanned")
                    }
                }
                .addOnCanceledListener {
                    processListener?.onCanceled()
                }
                .addOnFailureListener {
                    processListener?.onFailed(it)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                    processListener?.onCompleted()
                    processing = false
                }
        }
    }

    fun setProcessListener(listener: ProcessListener) {
        processListener = listener
    }

    fun getAnalyzerExecutor(): Executor = executor

    interface ProcessListener {
        fun onSucceed(results: List<Barcode>,inputImage: InputImage)
        fun onCanceled()
        fun onCompleted()
        fun onFailed(exception: Exception)
    }

    abstract class ProcessListenerAdapter : ProcessListener {
        override fun onSucceed(results: List<Barcode>,inputImage: InputImage) {}

        override fun onCanceled() {}

        override fun onCompleted() {}

        override fun onFailed(exception: Exception) {
            exception.message
        }
    }
}
