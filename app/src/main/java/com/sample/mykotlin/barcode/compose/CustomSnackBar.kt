package com.sample.mykotlin.barcode.compose


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sample.mykotlin.R
import com.sample.mykotlin.ui.theme.gray
import com.sample.mykotlin.ui.theme.white


@Composable
fun ShowSnackBar(
    message: String
) {
    Snackbar(containerColor = white, modifier = Modifier
        .padding(dimensionResource(id = R.dimen.fontsize_10dp)).fillMaxWidth().height(70.dp)
        .clip(
            shape = RoundedCornerShape(
                dimensionResource(id = R.dimen.fontsize_10dp)
            )
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painterResource(id = R.drawable.success),
                contentDescription = stringResource(id = R.string.success_icon),
                modifier = Modifier.weight(0.5f)
            )

            Text(
                text = message,
                color = gray,
                fontSize = dimensionResource(id = R.dimen.subtext_fontsize).value.sp,
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Normal
            )
            Image(
                painterResource(id = R.drawable.arrow),
                contentDescription = stringResource(id = R.string.arrowIcon),
                modifier = Modifier.weight(0.5f)
            )

        }
    }
}
@Composable
fun CustomSnackBar(barcode:String) {
    val snackState = remember { SnackbarHostState() }
    val customSnackBar = stringResource(id = R.string.custom_snackbar)
    LaunchedEffect(barcode) {

        snackState.showSnackbar(
            customSnackBar, duration = SnackbarDuration.Short
        )
    }
    SnackbarHost(
        hostState = snackState
    ) { snackBarData: SnackbarData ->
        ShowSnackBar(
                barcode,
            )


    }
}
