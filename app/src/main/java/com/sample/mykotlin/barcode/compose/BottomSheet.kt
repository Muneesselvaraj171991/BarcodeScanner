package com.sample.mykotlin.barcode.compose

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionLayoutDebugFlags
import com.sample.mykotlin.R
import com.sample.mykotlin.barcode.BarcodeScanViewModel
import com.sample.mykotlin.ui.theme.black
import com.sample.mykotlin.ui.theme.white
import java.util.EnumSet
import kotlin.math.abs


@SuppressLint("UnsafeOptInUsageError")
@OptIn(ExperimentalMotionApi::class)
@Composable
fun BottomSheet(viewModel: BarcodeScanViewModel) {
    Column {

        var isScanButtonPressed by remember { mutableStateOf(true) }

        val toolBarTitle: String? = viewModel.toolBarTitleChange.observeAsState().value

        var animateButton by remember { mutableStateOf(false) }

        val buttonAnimationProgress by animateFloatAsState(

            targetValue = if (animateButton) 1f else 0f,

            animationSpec = tween(1000)
        )
        val search: String = stringResource(id = R.string.scan_search)
        val handla: String = stringResource(id = R.string.barcode_scan)

        // LOCAL CONSTANT TO DETERMINE SWIPE DIRECTION
        val SWIPE_RIGHT = 0
        val SWIPE_LEFT = 1;
        var direction by remember { mutableStateOf(-1) }



    MotionLayout(
            ConstraintSet(
                """ {


guide1 : {
type: 'vGuideline',
percent: 0.4
},
btn_handla: {
width: 'wrap',
height: 'wrap',
start: ['guide1', 'end'],
},
btn_search: {
width: 'wrap',
height: 'wrap',
start: ['btn_handla', 'end'],
}
} """
            ),

            ConstraintSet(
                """ {
guide1 : {
type: 'vGuideline',
percent: 0.64
},
btn_handla: {
width: 'wrap',
height: 'wrap',
end: ['btn_search', 'start']
},

btn_search: {
width: 'wrap',
height: 'wrap',
start: ['guide1', 'start'],
}
} """
            ),
            progress = buttonAnimationProgress,
            debug = EnumSet.of(MotionLayoutDebugFlags.NONE),
            modifier = Modifier
                .fillMaxWidth()
                .size(dimensionResource(id = R.dimen.motionlayout_size))
                .background(black.copy(alpha = 0.6f))
                .pointerInput(Unit) {
                    detectDragGestures(onDrag = { change, dragAmount ->
                        change.consume()

                        val (x, y) = dragAmount
                        if (abs(x) > abs(y)) {
                            when {
                                x > 0 -> {
                                    //right
                                    direction = SWIPE_RIGHT
                                }
                                x < 0 -> {
                                    // left
                                    direction = SWIPE_LEFT
                                }
                            }
                        }

                    }, onDragEnd = {
                        when (direction) {
                            SWIPE_RIGHT-> {
                                isScanButtonPressed = true
                                viewModel.updateToolbarName(handla)
                                animateButton = false
                            }
                            SWIPE_LEFT -> {
                                isScanButtonPressed = false
                                viewModel.updateToolbarName(search)
                                animateButton = true
                            }
                        }
                    })
                }


        ) {


            Row(
                modifier = Modifier
                    .padding(top = dimensionResource(id = R.dimen.padding_))
                    .layoutId("btn_handla"),
                horizontalArrangement = Arrangement.Center

            ) {
                Image(
                    painter = painterResource(
                        id = if (isScanButtonPressed) {
                            R.drawable.bucket_selected
                        } else {
                            R.drawable.bucket_item_un_selected
                        }
                    ),
                    contentDescription = "",

                    modifier = Modifier
                        .clickable {
                            if (!isScanButtonPressed) {
                                isScanButtonPressed = true
                                viewModel.updateToolbarName(handla)
                                animateButton = !animateButton
                            }
                        }


                )

                Image(
                    painter = painterResource(
                        id = if (!isScanButtonPressed) {
                            R.drawable.search_selected
                        } else {
                            R.drawable.search_unselected
                        }
                    ),
                    contentDescription = "",
                    modifier = Modifier

                        .clickable {
                            if (isScanButtonPressed) {
                                isScanButtonPressed = false
                                viewModel.updateToolbarName(search)
                                animateButton = !animateButton
                            }

                        }
                )

            }


        }


        Row(
            modifier = Modifier
                .background(black.copy(alpha = 0.6f))
                .fillMaxWidth()
                .padding(bottom = dimensionResource(id = R.dimen.padding_))
        ) {
            toolBarTitle?.let {
                Text(
                    textAlign = TextAlign.Center,
                    text = it,
                    modifier = Modifier

                        .fillMaxWidth(),
                    style = MaterialTheme.typography.subtitle2.copy(color = white)
                )
            }
        }

    }

}
