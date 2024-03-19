package org.skynetsoftware.avnlauncher.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.skynetsoftware.avnlauncher.app.generated.resources.Res
import org.skynetsoftware.avnlauncher.app.generated.resources.searchLabel

@OptIn(ExperimentalResourceApi::class)
@Composable
fun Search(
    searchQuery: String,
    setSearchQuery: (searchQuery: String) -> Unit,
) {
    Box(
        modifier = Modifier.padding(top = 5.dp, bottom = 5.dp),
    ) {
        OutlinedTextFieldWithPadding(
            modifier = Modifier.padding(0.dp).fillMaxHeight(),
            value = searchQuery,
            onValueChange = {
                setSearchQuery(it)
            },
            placeholder = {
                Text(
                    text = stringResource(Res.string.searchLabel),
                    style = MaterialTheme.typography.body2,
                )
            },
            textStyle = TextStyle(
                color = MaterialTheme.colors.onPrimary,
            ),
            singleLine = true,
            trailingIcon = {
                if (searchQuery.isNotBlank()) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "clear text",
                        modifier = Modifier.clickable {
                            setSearchQuery("")
                        },
                    )
                }
            },
            contentPadding = PaddingValues(8.dp),
        )
    }
}
