package org.skynetsoftware.avnlauncher.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.skynetsoftware.avnlauncher.app.generated.resources.Res
import org.skynetsoftware.avnlauncher.app.generated.resources.star_empty
import org.skynetsoftware.avnlauncher.app.generated.resources.star_full

private const val RATING_MAX = 5

@OptIn(ExperimentalResourceApi::class)
@Composable
fun RatingBar(
    modifier: Modifier = Modifier,
    rating: Int,
    onClick: (rating: Int) -> Unit,
) {
    Row(
        modifier,
    ) {
        for (i in 0 until RATING_MAX) {
            Image(
                painter = painterResource(if (i < rating) Res.drawable.star_full else Res.drawable.star_empty),
                modifier = Modifier.size(20.dp).clickable {
                    onClick(i + 1)
                },
                contentDescription = null,
            )
        }
    }
}
