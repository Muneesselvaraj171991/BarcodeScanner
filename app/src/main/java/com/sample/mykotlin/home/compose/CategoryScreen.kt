package com.sample.mykotlin.home.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.sample.mykotlin.R
import com.sample.mykotlin.home.data.Item
import com.sample.mykotlin.home.data.ItemsData
import com.sample.mykotlin.ui.theme.*


@Composable
fun CategoryScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            CategoryView()
        }
        item {
            Row(
                modifier = Modifier.padding(start = dimensionResource(id = R.dimen.padding_), end = dimensionResource(id = R.dimen.padding_), bottom = dimensionResource(id = R.dimen.padding_)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.popular_items),
                    fontSize = dimensionResource(id = R.dimen.subtext_fontsize).value.sp,
                    modifier = Modifier.weight(1f),
                    color = gray
                )
                Text(
                    text = stringResource(id = R.string.view_all),
                    style = MaterialTheme.typography.subtitle2.copy(color = colorPrimary)
                )
            }
        }
        item {
            PopularItemsList()
        }
    }
}



@Composable
private fun CategoryView() {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(
        dimensionResource(id = R.dimen.padding_) )) {
        RoundedCornerIconButton(
            modifier = Modifier.weight(1f),
        )
        Spacer(modifier = Modifier.size( dimensionResource(id = R.dimen.fontsize_10dp)))
        RoundedCornerIconButton(
            modifier = Modifier.weight(1f),
        )
        Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.fontsize_10dp)))
        RoundedCornerIconButton(
            modifier = Modifier.weight(1f),
        )
        Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.fontsize_10dp)))
        RoundedCornerIconButton(
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
fun RoundedCornerIconButton(modifier: Modifier) {
    Box(
        modifier
            .background(everGreen.copy(alpha = 0.3f), shape = CircleShape),
    ) {
        IconButton(
            onClick = { }, modifier = Modifier
                .align(Alignment.Center)
                .padding(dimensionResource(id = R.dimen.fontsize_10dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_cart),
                contentDescription = "rounded_corner_icon_button"
            )
        }
    }
}

@Composable
private fun PopularItemsList() {
    LazyRow(
        modifier = Modifier.fillMaxWidth()
    ) {
        items(ItemsData.list.size) {
            ItemList(ItemsData.list[it])
        }
    }
}

@Composable
private fun ItemList(items: Item) {
    Card(
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.fontsize_16dp)),
        backgroundColor = white,
        modifier = Modifier
            .padding(dimensionResource(id = R.dimen.fontsize_8dp))
            .width(dimensionResource(id = R.dimen.card_fontsize))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.fontsize_10dp)),
        ) {

            Image(
                modifier = Modifier.size(dimensionResource(id = R.dimen.itemImage_fontszie)),
               painter= painterResource(id = items.image),
                contentDescription = stringResource(id = R.string.item_Image)
            )
            Row(modifier = Modifier.padding(top = dimensionResource(id = R.dimen.padding_))) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = items.name,
                        style = TextStyle(
                            color = gray,
                            fontSize = dimensionResource(id = R.dimen.subtext_fontsize).value.sp,
                        )
                    )
                    Text(

                        text = items.price,
                        style = TextStyle(
                            color = pricecolor,
                            fontSize = dimensionResource(id = R.dimen.subtext_fontsize).value.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                }


                Box(
                    modifier = Modifier
                        .background(
                            color = colorPrimary,
                            shape = RoundedCornerShape(dimensionResource(id = R.dimen.fontsize_10dp))
                        )
                ) {


                    Icon(
                        Icons.Default.Add,
                        tint = Color.White,
                        modifier = Modifier.padding(dimensionResource(id = R.dimen.fontsize_8dp)),
                        contentDescription = stringResource(id = R.string.icon_AddImage)
                    )
                }
            }
        }
    }
}