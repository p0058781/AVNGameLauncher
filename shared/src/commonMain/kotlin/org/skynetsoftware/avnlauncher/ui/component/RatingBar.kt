package org.skynetsoftware.avnlauncher.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun RatingBar(
    modifier: Modifier = Modifier,
    rating: Int,
    onClick: (rating: Int) -> Unit
) {

    Row(
        modifier
    ) {
        for (i in 0 until 5) {
            Image(
                painter = painterResource(if (i < rating) "star_full.png" else "star_empty.png"),
                modifier = Modifier.size(20.dp).clickable {
                    onClick(i + 1)
                },
                contentDescription = null,
            )
        }
    }
}
