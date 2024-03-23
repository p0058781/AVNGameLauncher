package org.skynetsoftware.avnlauncher.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.skynetsoftware.avnlauncher.app.generated.resources.Res
import org.skynetsoftware.avnlauncher.app.generated.resources.star_filled
import org.skynetsoftware.avnlauncher.app.generated.resources.star_outlined

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
                painter = painterResource(if (i < rating) Res.drawable.star_filled else Res.drawable.star_outlined),
                colorFilter = ColorFilter.tint(MaterialTheme.colors.primary),
                modifier = Modifier.clickable {
                    onClick(i + 1)
                },
                contentDescription = null,
            )
        }
    }
}
